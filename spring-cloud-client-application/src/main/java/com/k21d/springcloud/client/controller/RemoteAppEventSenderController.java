package com.k21d.springcloud.client.controller;

import com.k21d.springcloud.client.event.RemoteAppEvent;
import com.netflix.loadbalancer.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Service;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 远程应用控制器
 */
@RestController
public class RemoteAppEventSenderController implements ApplicationEventPublisherAware {
    private ApplicationEventPublisher applicationEventPublisher;
    @Value("spring.application.name")
    private String currentAppName;

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/send/remote/event")
    public String sendEvent(@RequestParam String message){
        applicationEventPublisher.publishEvent(message);
        return "success";
    }
    @PostMapping("/send/remote/evnet/{appName}")
    public String sendAppCluster(@PathVariable String appName,
                                 @RequestBody Map data){
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances(appName);
        RemoteAppEvent remoteAppEvent = new RemoteAppEvent(currentAppName, data, appName, serviceInstances);
        //发送事件给当前上下文
        applicationEventPublisher.publishEvent(remoteAppEvent);
        return "ok";
    }

    @PostMapping("/send/remote/evnet/{appName}/{ip}/{port}")
    public String sendAppInstance(@PathVariable String appName,
                                 @PathVariable String ip,
                                 @PathVariable String port,
                                 @RequestBody Object data){
        ServiceInstance serviceInstance = new DefaultServiceInstance(appName,ip,Integer.valueOf(port),false);
        RemoteAppEvent remoteAppEvent = new RemoteAppEvent(currentAppName, data, appName, Arrays.asList(serviceInstance));
        applicationEventPublisher.publishEvent(remoteAppEvent);
        return "ok";
    }
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
