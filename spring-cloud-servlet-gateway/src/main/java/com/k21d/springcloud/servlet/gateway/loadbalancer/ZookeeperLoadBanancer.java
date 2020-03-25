package com.k21d.springcloud.servlet.gateway.loadbalancer;

import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.zookeeper.discovery.ZookeeperServer;

import java.util.List;

public class ZookeeperLoadBanancer extends BaseLoadBalancer {
    private DiscoveryClient discoveryClient;
    @Value("${spring.application.name}")
    private String currentApplicationName;

    public ZookeeperLoadBanancer(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
        updateServers();
    }

    public void updateServers(){
        discoveryClient.getServices().stream().filter(service->!service.equals(currentApplicationName)).forEach(serviceName->{
            List<ServiceInstance> serviceInstances = discoveryClient.getInstances(serviceName);
            serviceInstances.forEach(serviceInstance -> {
                addServer(new Server(serviceInstance.getHost(),serviceInstance.getPort()));
            });
        });

    }
    @Override
    public Server chooseServer(Object key) {
        return super.chooseServer(key);
    }

    @Override
    public String choose(Object key) {
        return super.choose(key);
    }
}
