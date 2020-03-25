package com.k21d.springcloud.client.controller;

import com.k21d.springcloud.client.annotation.CustomizedLoadBalanced;
import com.k21d.springcloud.client.loadbalance.LoadBalanceInterceptor;
import com.k21d.springcloud.client.service.feign.clients.SayingService;
import com.k21d.springcloud.client.service.rest.clients.SayingRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ClientController {
    @Autowired
    private SayingService sayingService;
//    @Autowired
//    private SayingRestService sayingRestService;

    @Autowired
    @CustomizedLoadBalanced
    private RestTemplate restTemplate;
    /**
     * inject Ribbon RestTemplate Bean
     */
    @Autowired
    @LoadBalanced
    private RestTemplate lbRestTemplate;

    @Value("${spring.application.name}")
    private String currentServiceName;

    @Autowired
    private DiscoveryClient discoveryClient;

    private volatile Set<String> targetUrls = new HashSet<>();

//    private volatile Map<String,Set<String>> targetUrlsCache = new HashMap<>();
//    @Scheduled(fixedRate = 10*1000)
//    public void updateTargetUrlsCache(){
//        Map<String,Set<String>> oldTagetUrlsCache = this.targetUrlsCache;
//        Map<String,Set<String>> newTagetUrlsCache = new HashMap<>();
//        discoveryClient.getServices().forEach(serviceName->{
//            List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
//            Set<String> newTargetUrls = instances
//                    .stream()
//                    .map(s -> "http://" + s.getHost() + ":" + s.getPort())
//                    .collect(Collectors.toSet());
//            newTagetUrlsCache.put(serviceName,newTargetUrls);
//        });
//
//        this.targetUrlsCache = newTagetUrlsCache;
//        oldTagetUrlsCache.clear();
//    }
//    @Scheduled(fixedRate = 10*1000)
//    public void updateTargetUrls(){
//        List<ServiceInstance> instances = discoveryClient.getInstances(currentServiceName);
//        Set<String> newTargetUrls = instances
//                .stream()
//                .map(s -> "http://" + s.getHost() + ":" + s.getPort())
//                .collect(Collectors.toSet());
//        Set<String> oldTargetUrls  = this.targetUrls;
//        this.targetUrls = newTargetUrls;
//        oldTargetUrls.clear();
//    }

    @GetMapping("/invoke/{serviceName}/say")
    public String invokeSay(@PathVariable String serviceName,@RequestParam String message){
        //服务器列表
        //轮询列表
        //选择其中一台服务器
        //RestTemplate发送请求到服务器
        //输出响应
        return restTemplate.getForObject("/"+serviceName+"/say?message="+message, String.class);
    }
    @GetMapping("/loadbanlance/invoke/{serviceName}/say")
    public String lbInvokeSay(@PathVariable String serviceName,@RequestParam String message){
        return lbRestTemplate.getForObject("http://"+serviceName+"/say?message="+message, String.class);
    }

    @GetMapping("/feign/say")
    public String feignSay(@RequestParam String message){
        return sayingService.say(message);
    }
//    @GetMapping("/rest/say")
//    public String restSay(@RequestParam String message){
//        return sayingRestService.say(message);
//    }
    @Bean
    public ClientHttpRequestInterceptor interceptor(){
        return new LoadBalanceInterceptor();
    }
    @LoadBalanced
    @Bean
    public RestTemplate loadBalanceTemplate(){
        return new RestTemplate();
    }
    @Bean
    @Autowired
    @CustomizedLoadBalanced
    public RestTemplate restTemplate(ClientHttpRequestInterceptor interceptor){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Arrays.asList(interceptor));
        return restTemplate;
    }
    @Bean
    @Autowired
    public Object customizer(@CustomizedLoadBalanced Collection<RestTemplate> restTemplates,
                             ClientHttpRequestInterceptor interceptor){
        restTemplates.forEach(r->{
            r.setInterceptors(Arrays.asList(interceptor));
        });
        return new Object();
    }
}
