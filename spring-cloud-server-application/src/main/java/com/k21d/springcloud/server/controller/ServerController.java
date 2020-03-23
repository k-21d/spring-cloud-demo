package com.k21d.springcloud.server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ServerController {

    @GetMapping("/say")
    public String say(@RequestParam String message){
        System.out.println("ServerController receive say:"+message);
        return "hello, "+ message;
    }


}
