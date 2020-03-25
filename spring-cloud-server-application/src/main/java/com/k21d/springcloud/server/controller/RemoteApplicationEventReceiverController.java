package com.k21d.springcloud.server.controller;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RemoteApplicationEventReceiverController implements ApplicationEventPublisherAware {
    private ApplicationEventPublisher applicationEventPublisher;
    @PostMapping("/reveive/remote/event")
    public String receive(@RequestBody Map<String,Object> data){
        //事件的发送者
        String sender = (String) data.get("sender");
        //事件的数据内容
        String value = (String) data.get("value");
        //事件类型
        String type = (String) data.get("type");
        //接收到对方内容，同样也要发送事件到本地做处理
        applicationEventPublisher.publishEvent(value);
        return "revceived";
    }

    public static class SenderRemoteAppEvent extends ApplicationEvent{
        private final String sender;

        public SenderRemoteAppEvent(String sender,Object object) {
            super(object);
            this.sender = sender;
        }

        public String getSender() {
            return sender;
        }
    }
    @EventListener
    public void onEvent(SenderRemoteAppEvent event){
        System.out.println(event.getSender()+":"+event);
    }
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
