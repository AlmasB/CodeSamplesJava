package com.almasb.spring.basic;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessagePrinter {

    private MessageService service;
    private Supplier<String> supplier;

    @Autowired
    public MessagePrinter(Supplier<String> supplier) {
        this.supplier = supplier;
    }

    public MessagePrinter(MessageService service) {
        this.service = service;
    }

    public void print() {
        System.out.println(supplier.get());
    }

    public void printMessage() {
        System.out.println(this.service.getMessage());
    }
}