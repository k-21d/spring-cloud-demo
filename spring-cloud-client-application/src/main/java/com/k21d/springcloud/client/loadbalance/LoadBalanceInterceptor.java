package com.k21d.springcloud.client.loadbalance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.stream.Collectors;

public class LoadBalanceInterceptor implements ClientHttpRequestInterceptor {
    @Autowired
    private DiscoveryClient discoveryClient;

    private volatile Map<String,Set<String>> targetUrlsCache = new HashMap<>();

    @Scheduled(fixedRate = 10*1000)
    public void updateTargetUrlsCache(){
        Map<String,Set<String>> oldTagetUrlsCache = this.targetUrlsCache;
        Map<String,Set<String>> newTagetUrlsCache = new HashMap<>();
        discoveryClient.getServices().forEach(serviceName->{
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
            Set<String> newTargetUrls = instances
                    .stream()
                    .map(s -> "http://" + s.getHost() + ":" + s.getPort())
                    .collect(Collectors.toSet());
            newTagetUrlsCache.put(serviceName,newTargetUrls);
        });

        this.targetUrlsCache = newTagetUrlsCache;
        oldTagetUrlsCache.clear();
    }
    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        //URI:${app-name}/uri
        URI requestURI = httpRequest.getURI();
        String path = requestURI.getPath();
        String[] parts = StringUtils.split(path.substring(1),"/");
        String serviceName = parts[0];
        String uri = parts[1];
        List<String> targetUrls = new LinkedList<>(this.targetUrlsCache.get(serviceName));
        int size = targetUrls.size();
        int index = new Random().nextInt(size);
        String targetUrl = targetUrls.get(index);

        String acutalUrl = targetUrl+"/"+uri+"?"+requestURI.getQuery();
//        List<HttpMessageConverter<?>> messageConverters = Arrays.asList(new ByteArrayHttpMessageConverter(),
//                new StringHttpMessageConverter());
//        RestTemplate restTemplate = new RestTemplate(messageConverters);
//        ResponseEntity<InputStream> entity = restTemplate.getForEntity(acutalUrl, InputStream.class);

        URL url = new URL(acutalUrl);
        URLConnection urlConnection = url.openConnection();
        InputStream responseBody = urlConnection.getInputStream();
        return new SimpleClientHttpResponse(responseBody,new HttpHeaders());
    }

    private static class SimpleClientHttpResponse implements ClientHttpResponse{
        private InputStream body;
        private HttpHeaders headers;

        @Override
        public HttpStatus getStatusCode() throws IOException {
            return HttpStatus.OK;
        }

        @Override
        public int getRawStatusCode() throws IOException {
            return 200;
        }

        @Override
        public String getStatusText() throws IOException {
            return "ok";
        }

        @Override
        public void close() {

        }

        @Override
        public InputStream getBody() throws IOException {
            return body;
        }

        @Override
        public HttpHeaders getHeaders() {
            return headers;
        }

        public SimpleClientHttpResponse(InputStream body, HttpHeaders headers) {
            this.body = body;
            this.headers = headers;
        }
    }
}
