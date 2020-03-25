package com.k21d.springcloud.client.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.context.ApplicationListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRemoteAppEventListener implements ApplicationListener<RemoteAppEvent> {

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public void onApplicationEvent(RemoteAppEvent remoteAppEvent) {
        Object source = remoteAppEvent.getSource();
        String appName = remoteAppEvent.getAppName();
        List<ServiceInstance> serviceInstances = remoteAppEvent.getServiceInstances();
        for (ServiceInstance serviceInstance : serviceInstances) {
            String rootURL = serviceInstance.isSecure()?"https://":"http://"
                    +serviceInstance.getHost()+":"
                    + serviceInstance.getPort();

            String url = rootURL + "/reveive/remote/event";
            Map<String,Object> data = new HashMap<>();
            data.put("sender",remoteAppEvent.getSender());
            data.put("value",source);
            data.put("type",remoteAppEvent.getClass().getName());
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, data, String.class);

        }

    }
}
