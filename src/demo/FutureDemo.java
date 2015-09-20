package com.almasb.java.framework.demo;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FutureDemo {
    public static void main(String[] args) {
        ExecutorService es = Executors.newFixedThreadPool(2);

        Future<Long> result = es.submit(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                Thread.sleep(2000);

                return 30L;
            }
        });

        try {
            // .get() blocks until the above call completed or exception thrown
            System.out.println("Result: " + result.get());
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        // shutdown the executor service
        es.shutdown();
    }
}
