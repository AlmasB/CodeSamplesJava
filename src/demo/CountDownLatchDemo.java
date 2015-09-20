package com.almasb.java.framework.demo;

import java.util.concurrent.CountDownLatch;

import javafx.beans.property.SimpleBooleanProperty;

public class CountDownLatchDemo {

    public static void main(String[] args) throws Exception {

        CountDownLatch latch = new CountDownLatch(3);

        SimpleBooleanProperty running = new SimpleBooleanProperty(true);


        new Thread(() -> {

            while (running.get()) {
                try {
                    Thread.sleep(1000);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                latch.countDown();
                System.out.println(latch.getCount());
            }
        }).start();

        latch.await();
        running.set(false);
        System.out.println("App finished");
    }
}
