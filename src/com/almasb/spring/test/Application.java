package com.almasb.spring.test;

import java.util.function.Supplier;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class Application {

    @Bean
    Supplier<String> myBean() {
        return new Supplier<String>() {
            public int i = 0;

            @Override
            public String get() {
                return i++ + "";
            }

        };
    }

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(Application.class);

        Class1 c = context.getBean(Class1.class);
        c.print();

        Class2 c2 = context.getBean(Class2.class);
        c2.print();
    }
}
