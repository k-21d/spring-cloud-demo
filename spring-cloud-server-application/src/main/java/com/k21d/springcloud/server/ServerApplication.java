package com.k21d.springcloud.server;

import com.k21d.springcloud.server.stream.SimpleMessageReceiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.SubscribableChannel;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;

@SpringBootApplication
@EnableDiscoveryClient
@EnableHystrix
@EnableAspectJAutoProxy(proxyTargetClass = true)//AOP
@EnableBinding(SimpleMessageReceiver.class)
public class ServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class,args);
    }

    @Autowired
    private SimpleMessageReceiver simpleMessageReceiver;

    @PostConstruct
    public void init(){
        SubscribableChannel subscribableChannel = simpleMessageReceiver.k21d();
        subscribableChannel.subscribe(message -> {
            MessageHeaders headers = message.getHeaders();
            String encoding = (String) headers.get("charest-encoding");
            try {
                System.out.println(new String((byte[]) message.getPayload(),encoding));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
    }
    @StreamListener("k21d")
    public void onMessage(byte[] data){
        System.out.println("byte[]:"+data);
    }
    @StreamListener("k21d")
    public void onMessage(String data){
        System.out.println("String:"+data);
    }
}
