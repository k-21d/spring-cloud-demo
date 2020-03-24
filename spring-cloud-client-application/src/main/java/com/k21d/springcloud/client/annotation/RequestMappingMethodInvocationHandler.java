package com.k21d.springcloud.client.annotation;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

public class RequestMappingMethodInvocationHandler implements InvocationHandler {
    private final String serviceName;
    private final DefaultParameterNameDiscoverer defaultParameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    private final BeanFactory beanFactory;

    public RequestMappingMethodInvocationHandler(String serviceName, BeanFactory beanFactory) {
        this.serviceName = serviceName;
        this.beanFactory = beanFactory;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         GetMapping getMapping = findAnnotation(method, GetMapping.class);
        if (getMapping != null){
            String[] uri = getMapping.value();
            StringBuilder urlBuilder = new StringBuilder("http://")
                    .append(serviceName).append("/")
                    .append(uri[0]);
            int count = method.getParameterCount();
            String[] paramNames = defaultParameterNameDiscoverer.getParameterNames(method);
            Class<?>[] parameterTypes = method.getParameterTypes();
            Annotation[][] annotations = method.getParameterAnnotations();
            StringBuilder queryString = new StringBuilder();
            //方法注解集合
            for (int i=0;i<count;i++){
                Annotation[] paramAnnotation = annotations[i];
                Class<?> paramType = parameterTypes[i];

                RequestParam requestParam = (RequestParam) paramAnnotation[0];
                if (requestParam!=null){
                    String paramName = paramNames[i];
                    String requestParamName = StringUtils.hasText(requestParam.value())?requestParam.value():paramName;
                    String requestParamValue = String.class.equals(paramType)
                            ?(String)args[i]:String.valueOf(args[i]);
                    queryString.append("&")
                            .append(requestParamName).append("=").append(requestParamValue);
                }
                if (StringUtils.hasText(queryString)){
                    urlBuilder.append("?").append(queryString);
                }
                String url = urlBuilder.toString();
                //RestTemplate "loadBalanceTemplate"
                //获得BeanFactory
                RestTemplate loadBalanceTemplate =  beanFactory.getBean("loadBalanceTemplate",RestTemplate.class);
                return loadBalanceTemplate.getForObject(url,method.getReturnType());
                }

        }
        return null;
    }
}
