package com.k21d.springcloud.server.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface SimpleMessageReceiver {
    @Input("k21d")
    SubscribableChannel k21d();
}
