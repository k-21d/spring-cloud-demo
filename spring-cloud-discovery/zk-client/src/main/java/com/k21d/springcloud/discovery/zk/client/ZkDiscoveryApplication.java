package com.k21d.springcloud.discovery.zk.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ZkDiscoveryApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZkDiscoveryApplication.class,args);
    }
}
