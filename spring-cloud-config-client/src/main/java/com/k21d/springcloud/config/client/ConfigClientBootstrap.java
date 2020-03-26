package com.k21d.springcloud.config.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@SpringBootApplication
@EnableDiscoveryClient
public class ConfigClientBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(ConfigClientBootstrap.class,args);
    }

}
