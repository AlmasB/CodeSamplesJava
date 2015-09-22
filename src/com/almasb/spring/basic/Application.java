package com.almasb.spring.basic;

import java.util.function.Supplier;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@ComponentScan
public class Application {

    @Bean
    MessageService mockMessageService() {
        return new MessageService() {
            @Override
            public String getMessage() {
                return "Hello World!";
            }
        };
    }

    @Bean
    public Supplier<String> simpleProducer() {
        return () -> "Hello Lambda!";
    }

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(
                Application.class);
        MessagePrinter printer = context.getBean(MessagePrinter.class);
        printer.print();
    }
}