package com.k21d.springcloud.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ClientController {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${spring.application.name}")
    private String currentServiceName;

    @Autowired
    private DiscoveryClient discoveryClient;

    private volatile Set<String> targetUrls = new HashSet<>();

    @Scheduled(fixedRate = 10*1000)
    public void updateTargetUrls(){
        List<ServiceInstance> instances = discoveryClient.getInstances(currentServiceName);
        Set<String> newTargetUrls = instances
                .stream()
                .map(s -> "http://" + s.getHost() + ":" + s.getPort())
                .collect(Collectors.toSet());
        Set<String> oldTargetUrls  = this.targetUrls;
        this.targetUrls = newTargetUrls;
        oldTargetUrls.clear();
    }

    @GetMapping("/invoke/say")
    public String invokeSay(@RequestParam String message){
        //服务器列表
        //轮询列表
        //选择其中一台服务器
        //RestTemplate发送请求到服务器
        //输出响应
        List<String> targetUrls = new ArrayList<>(this.targetUrls);
        int size = targetUrls.size();
        int index = new Random().nextInt(size);
        String targetUrl = targetUrls.get(index);
        return restTemplate.getForObject(targetUrl+"/say?message="+message+",from "+targetUrl,String.class);
    }

    @GetMapping("/say")
    public String say(@RequestParam String message){
        System.out.println("say:"+message);
        return "hello, "+ message;
    }


    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
