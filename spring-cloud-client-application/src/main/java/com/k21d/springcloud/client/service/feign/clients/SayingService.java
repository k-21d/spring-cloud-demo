package com.k21d.springcloud.client.service.feign.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "spring-cloud-server-application")
public interface SayingService {

    @GetMapping("/say")
    public String say(@RequestParam("message") String message);
}
