package com.almasb.java.framework.demo;

import java.util.List;

import com.almasb.common.encryption.AESEncryptor;
import com.almasb.java.io.ResourceManager;

public class AESEncryptorDemo {
    public static void main(String[] args) {
        /*String data = "This is text and that is my long version";
        String pass = "MyPass1ddddddddddddddddd";

        System.out.println(Arrays.toString(data.getBytes()));

        byte[] enc = AESEncryptor.encrypt(data.getBytes(), pass.toCharArray());

        System.out.println(Arrays.toString(enc));
        System.out.println("String: " + new String(enc));

        byte[] dec = AESEncryptor.decrypt(enc, pass.toCharArray());

        System.out.println(Arrays.toString(dec));
        System.out.println("String: " + new String(dec));*/


        // max 1883
        final int SIZE = 20;

        List<String> lines = ResourceManager.loadText("words.txt");
        //List<String> encryptedLines = new ArrayList<String>();
        byte[][] array = new byte[SIZE][];

        long start = System.nanoTime();

        for (int i = 0; i < SIZE; i++) {
            array[i] = AESEncryptor.encrypt(lines.get(i).getBytes(), "SomeLongPasswork123".toCharArray());
        }

        System.out.println("Finished encryption of " + SIZE + " words in " + (System.nanoTime() - start) / 1000000000.0 + " s");

        start = System.nanoTime();

        int count = 0;

        for (int i = 0; i < SIZE; i++) {
            String text = new String(AESEncryptor.decrypt(array[i], "SomeLongPasswork123".toCharArray()));
            if (!text.equals(lines.get(i)))
                count++;
        }

        System.out.println("Finished decryption of " + SIZE + " words in " + (System.nanoTime() - start) / 1000000000.0 + " s");
        System.out.println("Incorrect decryption count: " + count);
    }
}
