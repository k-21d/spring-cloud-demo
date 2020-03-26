package com.k21d.springcloud.config.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RestController
public class ConfigController {
    @Autowired
    private DiscoveryClient discoveryClient;
    @GetMapping("/services")
    public Set<String> getServices(){
        List<String> services = discoveryClient.getServices();
        return new LinkedHashSet<>(services);
    }
}
