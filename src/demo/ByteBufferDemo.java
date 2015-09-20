package com.almasb.java.framework.demo;

import java.nio.ByteBuffer;

public class ByteBufferDemo {

    public static void main(String[] args) {
        byte[] array = ByteBuffer.allocate(4).putInt(1246456).array();
        System.out.println(ByteBuffer.wrap(array).getInt());
    }
}
