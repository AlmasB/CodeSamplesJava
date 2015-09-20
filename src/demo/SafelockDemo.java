package com.almasb.java.framework.demo;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SafelockDemo {

    static class MyInt {
        public int value = 0;
        public final Lock lock = new ReentrantLock();
    }

    static MyInt c = new MyInt();

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (c.lock.tryLock()) {
                        try {
                            if (c.value == 100)
                                return;

                            c.value++;
                            System.out.println(Thread.currentThread().getName() + " c: " + c.value);
                        }
                        finally {
                            c.lock.unlock();
                        }

                        try {
                            Thread.sleep(200);
                        }
                        catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    else {
                        System.out.println(Thread.currentThread().getName() + ": C is locked");
                    }
                }

            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (c.lock.tryLock()) {
                        try {
                            if (c.value == 100)
                                return;

                            c.value++;
                            System.out.println(Thread.currentThread().getName() + " c: " + c.value);
                        }
                        finally {
                            c.lock.unlock();
                        }

                        try {
                            Thread.sleep(200);
                        }
                        catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    else {
                        System.out.println(Thread.currentThread().getName() + ": C is locked");
                    }
                }

            }
        }).start();
    }
}
