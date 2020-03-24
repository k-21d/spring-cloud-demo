package com.k21d.springcloud.client.service.rest.clients;

import com.k21d.springcloud.client.annotation.RestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestClient(name = "spring-cloud-server-application")
public interface SayingRestService {

    @GetMapping("/say")
    public String say(@RequestParam("message") String message);//请求参数和方法同名
}
