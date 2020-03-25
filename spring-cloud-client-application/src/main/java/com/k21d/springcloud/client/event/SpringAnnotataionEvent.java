package com.k21d.springcloud.client.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.EventListener;

public class SpringAnnotataionEvent {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.registerBean(SpringAnnotataionEvent.class);

        context.refresh();
        context.publishEvent(new MyApplicationEvent("hello,world"));
        context.close();
    }
    private static class MyApplicationEvent extends ApplicationEvent {

        public MyApplicationEvent(Object source) {
            super(source);
        }
    }
    @EventListener
    public void onMessage(MyApplicationEvent event){
        System.out.println("listen: "  + event.getSource());
    }
}
