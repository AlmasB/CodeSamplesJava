package com.almasb.spring.test;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Class1 {

    @Autowired
    public Supplier<String> sup;

    public void print() {
        System.out.println(sup.get());
    }
}
