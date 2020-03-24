package com.k21d.springcloud.client.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RestClient {
    /**
     * REST服务应用名称
     * @return
     */
    String name();
}
