package com.k21d.springcloud.servlet.gateway;

import com.k21d.springcloud.servlet.gateway.loadbalancer.ZookeeperLoadBanancer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
@ServletComponentScan
public class SpringCloudServletGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringCloudServletGatewayApplication.class,args);
    }
    @Bean
    public ZookeeperLoadBanancer zookeeperLoadBanancer(DiscoveryClient discoveryClient){
        return new ZookeeperLoadBanancer(discoveryClient);
    }
}
