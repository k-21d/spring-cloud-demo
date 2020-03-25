package com.k21d.springcloud.client.event;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public class RemoteAppEvent extends ApplicationEvent {
    /**
     * 事件传输类型HTTP、RPC、MQ
     */
    private String type;

    private String sender;

    private String appName;

    private List<ServiceInstance> serviceInstances;

    public RemoteAppEvent(String sender,Object source, String appName, List<ServiceInstance> serviceInstances) {
        super(source);
        this.sender = sender;
        this.appName = appName;
        this.serviceInstances = serviceInstances;
    }

    /**
     * POJO事件源
     * @param source
     */
    public RemoteAppEvent(Object source) {
        super(source);
    }

    public String getType() {
        return type;
    }

    public String getAppName() {
        return appName;
    }

    public List<ServiceInstance> getServiceInstances() {
        return serviceInstances;
    }

    public String getSender() {
        return sender;
    }
}
