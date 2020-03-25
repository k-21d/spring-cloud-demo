package com.k21d.springcloud.servlet.gateway.servlet;

import com.k21d.springcloud.servlet.gateway.loadbalancer.ZookeeperLoadBanancer;
import com.netflix.loadbalancer.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * 服务网关的路由规则
 * /{service-name}/{service-uri}
 * /a/hello->http://xxx/hello
 */
@WebServlet(name = "gateway", urlPatterns = "/gateway/*")
public class GatewayServlet extends HttpServlet {
    @Autowired
    private DiscoveryClient discoveryClient;

    private ServiceInstance randomChooseService(String serviceName){
        //获取服务实例列表
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances(serviceName);
        int size = serviceInstances.size();
        int index = new Random().nextInt(size);
        return serviceInstances.get(index);
    }
    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String[] parts = StringUtils.split(pathInfo.substring(1),"/");
        String serviceName = parts[0];
        String serviceURI = "/" + parts[1];
        ServiceInstance serviceInstance = randomChooseService(serviceName);
        //构建目标服务的URL
        String targetURL = buildTargetURL(serviceInstance,serviceURI,req);
        RestTemplate restTemplate = new RestTemplate();
        RequestEntity<byte[]> requestEntity = null;
        try {
            requestEntity = createRequestEntity(req, targetURL);
            ResponseEntity<byte[]> responseEntity = restTemplate.exchange(requestEntity, byte[].class);
            writeHeaders(responseEntity, resp);
            writeBody(responseEntity, resp);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void writeBody(ResponseEntity<byte[]> responseEntity, HttpServletResponse response) throws IOException {
        if (responseEntity.hasBody()) {
            byte[] body = responseEntity.getBody();
            // 输出二进值
            ServletOutputStream outputStream = response.getOutputStream();
            // 输出 ServletOutputStream
            outputStream.write(body);
            outputStream.flush();
        }
    }
    private void writeHeaders(ResponseEntity<byte[]> responseEntity, HttpServletResponse response) {
        // 获取相应头
        HttpHeaders httpHeaders = responseEntity.getHeaders();
        // 输出转发 Response 头
        for (Map.Entry<String, List<String>> entry : httpHeaders.entrySet()) {
            String headerName = entry.getKey();
            List<String> headerValues = entry.getValue();
            for (String headerValue : headerValues) {
                response.addHeader(headerName, headerValue);
            }
        }
    }
    private RequestEntity<byte[]> createRequestEntity(HttpServletRequest req, String targetURL) throws URISyntaxException, IOException {
        String method = req.getMethod();
        HttpMethod httpMethod = HttpMethod.resolve(method);
        byte[] body = createRequestBody(req);
        MultiValueMap<String,String> headers = createRequestHeaders(req);
        RequestEntity<byte[]> requestEntity = new RequestEntity<>(body, headers, httpMethod, new URI(targetURL));
        return requestEntity;
    }

    private MultiValueMap<String, String> createRequestHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        List<String> headerNames = Collections.list(request.getHeaderNames());
        for (String headerName : headerNames) {
            List<String> headerValues = Collections.list(request.getHeaders(headerName));
            for (String headerValue : headerValues) {
                headers.add(headerName, headerValue);
            }
        }
        return headers;
    }
    private byte[] createRequestBody(HttpServletRequest request) throws IOException {
        InputStream inputStream = request.getInputStream();
        return StreamUtils.copyToByteArray(inputStream);
    }
    private String buildTargetURL(ServiceInstance serviceInstance, String serviceURI,HttpServletRequest request) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(serviceInstance.isSecure()?"https://":"http://")
                .append(serviceInstance.getHost())
                .append(":")
                .append(serviceInstance.getPort())
                .append(serviceURI);
        String queryString = request.getQueryString();
        if (StringUtils.hasText(queryString)) {
            urlBuilder.append("?").append(queryString);
        }
        return urlBuilder.toString();
    }
}
