package com.k21d.springcloud.server.controller;

import com.k21d.springcloud.server.annotation.SemaphoreCircuitBreaker;
import com.k21d.springcloud.server.annotation.TimeOutCircuitBreaker;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.*;


@RestController
public class ServerController {
    private final static Random random = new Random();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    @HystrixCommand(fallbackMethod = "errorContent",
            commandProperties = {
                    @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="100")
            })
    @GetMapping("/say")
    public String say(@RequestParam String message) throws InterruptedException {
        int value = random.nextInt(200);
        System.out.println("say() costs"+value+"ms");
        Thread.sleep (value);
        System.out.println("ServerController receive say:"+message);
        return "hello, "+ message;
    }
    @GetMapping("/say2")
    public String say2(@RequestParam String message) throws Exception {
        Future<String> future = executorService.submit(() -> {
            return doSay2(message);
        });
        // 100 毫秒超时
        String returnValue = null;
        try {
            returnValue = future.get(100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            // 超级容错 = 执行错误 或 超时
            returnValue = errorContent(message);
        }
        return returnValue;
    }
    public String doSay2(String message) throws Exception {
        int value = random.nextInt(200);
        System.out.println("say() costs"+value+"ms");
        Thread.sleep (value);
        System.out.println("ServerController receive say:"+message);
        String returnValue = "hello, "+ message;

        return returnValue;
    }
    public String errorContent(String message){
        return "Fault";
    }

    @GetMapping("/middle/say")
    public String middleSay(@RequestParam String message) throws Exception {
        Future<String> future = executorService.submit(() -> {
            return doSay2(message);
        });
        // 100 毫秒超时
        String returnValue = null;

        try {
            returnValue = future.get(100, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true); // 取消执行
            throw e;
        }
        return returnValue;
    }

    @GetMapping("/advanced/say")
    public String advancedSay(@RequestParam String message) throws Exception {
        return doSay2(message);
    }
    @GetMapping("/advanced/say2")
    @TimeOutCircuitBreaker(timeout = 100)
    public String advancedSay2(@RequestParam String message) throws Exception {
        return doSay2(message);
    }
    @GetMapping("/advanced/say3")
    @SemaphoreCircuitBreaker(5)
    public String advancedSay3(@RequestParam String message) throws Exception {
        return doSay2(message);
    }
}
