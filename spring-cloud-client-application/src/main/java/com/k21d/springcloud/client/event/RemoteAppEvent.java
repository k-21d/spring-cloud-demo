package com.k21d.springcloud.client.event;

import org.springframework.context.ApplicationEvent;

public class RemoteAppEvent extends ApplicationEvent {
    /**
     * POJO事件源
     * @param source
     */
    public RemoteAppEvent(Object source) {
        super(source);
    }
}
