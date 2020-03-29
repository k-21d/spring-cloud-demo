package com.k21.springcloud.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthServerBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(AuthServerBootstrap.class,args);
    }
}
