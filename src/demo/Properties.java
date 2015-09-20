package com.almasb.java.framework.demo;

import java.util.ResourceBundle;

public class Properties {

    private static final ResourceBundle resources = ResourceBundle
            .getBundle("com.almasb.java.framework.demo.test");

    public static void main(String[] args) {
        resources.keySet().forEach(key -> System.out.println(key + "=" + resources.getString(key)));
    }
}
