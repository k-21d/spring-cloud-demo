package com.k21d.springcloud.client.controller;

import com.k21d.springcloud.client.stream.SimpleMessageService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MessageController {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private SimpleMessageService simpleMessageService;

    @GetMapping
    public String send(@RequestParam String message){
        rabbitTemplate.convertAndSend(message);
        return "ok";
    }
    @GetMapping("/stream/send")
    public boolean streamSend(@RequestParam String message){
        MessageChannel messageChannel = simpleMessageService.k21d();
        Map<String,Object> headers = new HashMap<>();
        headers.put("charset-encoding","UTF-8");
        GenericMessage<String> msg = new GenericMessage<>(message,headers);
        return messageChannel.send(msg);
    }
}
