package com.k21d.springcloud.service.provider;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class EchoServiceController {

    // 外部化配置其实是有点不靠谱的 - 它并非完全静态，也不一定及时返回

    private final Environment environment;

    public EchoServiceController(Environment environment) {
        this.environment = environment;
    }

    private String getPort() {
        return environment.getProperty("local.server.port");
    }

    @GetMapping("/hello")
    public String hello(){
        return "hellohello";
    }


    public String fallbackHello() {
        return "FALLBACK";
    }

    // Hystrix 配置文档：https://github.com/Netflix/Hystrix/wiki/Configuration
    @HystrixCommand(
            fallbackMethod = "fallback",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.strategy", value = "THREAD"),
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "50"),
            })
    @GetMapping(value = "/echo/{message}")
    public String echo(@PathVariable String message) {
        await();
        return "[ECHO:" + getPort() + "] " + message;
    }


    public String fallback(String abc) {
        return "FALLBACK - " + abc;
    }

    public String fallback(String abc, boolean value) {
        return "FALLBACK - " + abc;
    }

    private final Random random = new Random();

    private void await() {
        long wait = random.nextInt(100);
        System.out.printf("[当前线程 : %s] 当前方法执行(模型) 消耗 %d 毫秒\n", Thread.currentThread().getName(), wait);
        try {
            Thread.sleep(wait);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
