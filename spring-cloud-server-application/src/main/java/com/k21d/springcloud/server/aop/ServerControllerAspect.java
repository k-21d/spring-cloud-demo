package com.k21d.springcloud.server.aop;

import com.k21d.springcloud.server.annotation.SemaphoreCircuitBreaker;
import com.k21d.springcloud.server.annotation.TimeOutCircuitBreaker;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.*;

import static java.util.concurrent.Executors.newFixedThreadPool;

@Aspect
@Component
public class ServerControllerAspect {
    private ExecutorService executorService = newFixedThreadPool(20);
    private Semaphore semaphore;

    @Around("execution(* com.k21d.springcloud.server.controller.ServerController.advancedSay(..)) && args(message) ")
    public Object advancedSayInTimeout(ProceedingJoinPoint joinPoint,String message) throws Throwable{
        return doInvoke(joinPoint, message, 100);

    }

    @Around("execution(* com.k21d.springcloud.server.controller.ServerController.advancedSay2(..)) && args(message) && @annotation(timeOutCircuitBreaker)")
    public Object advancedSayInTimeout2(ProceedingJoinPoint joinPoint, String message, TimeOutCircuitBreaker timeOutCircuitBreaker) throws Throwable{
        long timeout = timeOutCircuitBreaker.timeout();
        return doInvoke(joinPoint, message, timeout);
    }
    @Around("execution(* com.k21d.springcloud.server.controller.ServerController.advancedSay3(..)) && args(message) && @annotation(semaphoreCircuitBreaker)")
    public Object advancedSayInTimeout2(ProceedingJoinPoint joinPoint, String message, SemaphoreCircuitBreaker semaphoreCircuitBreaker) throws Throwable{
        int value = semaphoreCircuitBreaker.value();
        if (semaphore == null) {
            semaphore = new Semaphore(value);
        }
        Object returnValue = null;
        try {
            if (semaphore.tryAcquire()) {
                returnValue = joinPoint.proceed(new Object[]{message});
                Thread.sleep(1000);
            } else {
                returnValue = errorContent();
            }
        } finally {
            semaphore.release();
        }

        return returnValue;
    }
    @PreDestroy
    public void destory(){
        executorService.shutdown();
    }
    private Object doInvoke(ProceedingJoinPoint joinPoint, String message, long timeout) {
        Future<Object> future = executorService.submit(() -> {
            Object returnValue = null;
            try {
                returnValue = joinPoint.proceed(new Object[]{message});
            } catch (Throwable ex) {
            }
            return returnValue;
        });

        Object returnValue = null;
        try {
            returnValue = future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException  | InterruptedException | ExecutionException e) {
            future.cancel(true); // 取消执行
            returnValue = errorContent();
        }
        return returnValue;
    }

    private Object errorContent() {
        return "Fault";
    }

}
