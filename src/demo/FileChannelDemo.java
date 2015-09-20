package com.almasb.java.framework.demo;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;

public class FileChannelDemo {

    public static void main(String[] args) throws Exception {

        ByteBuffer buffer = ByteBuffer.allocate(10);
        ByteBuffer buffer2 = ByteBuffer.allocate(16);

        try (FileChannel fc = FileChannel.open(Paths.get("res/fraction.js"))) {
            int len;
            do {
                len = fc.read(buffer);
            } while (len != -1 && buffer.hasRemaining());

            do {
                len = fc.read(buffer2);
            } while (len != -1 && buffer2.hasRemaining());
        }

        System.out.println(new String(buffer.array()));
        System.out.println(new String(buffer2.array()));
    }

}
