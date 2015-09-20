package com.almasb.java.framework.demo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class BulkOperationDemo {

    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<String>();

        list.add("some text");
        list.add("some other");
        list.add("himan");
        list.add("hitman");

        list.stream().map(BulkOperationDemo::allUP).collect(Collectors.toList()).forEach(System.out::println);

        System.out.println(LocalTime.now());
        System.out.println(LocalDate.now());
    }

    public static String allUP(String s) {
        return s.toUpperCase();
    }
}
