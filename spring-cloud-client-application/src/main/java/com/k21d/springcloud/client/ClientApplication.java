package com.k21d.springcloud.client;

import com.k21d.springcloud.client.annotation.EnableRestClients;
import com.k21d.springcloud.client.service.feign.clients.SayingService;
import com.k21d.springcloud.client.service.rest.clients.SayingRestService;
import com.k21d.springcloud.client.stream.SimpleMessageService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@EnableFeignClients(clients = SayingService.class)
@EnableBinding(SimpleMessageService.class)
//@EnableRestClients(clients = SayingRestService.class)
public class ClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class,args);
    }
}
