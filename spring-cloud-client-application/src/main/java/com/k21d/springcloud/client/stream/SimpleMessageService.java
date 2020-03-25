package com.k21d.springcloud.client.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface SimpleMessageService {

    @Output("k21d") //Channel name
    MessageChannel k21d();
}
