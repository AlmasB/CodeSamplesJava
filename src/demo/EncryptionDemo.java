package com.almasb.java.framework.demo;

import java.security.AlgorithmParameters;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class EncryptionDemo {


    public static void main(String[] args) throws Exception {
        /*byte[] input = "Hello, how are you doing?".getBytes();

        System.out.println(Arrays.toString(input));

        byte[] keyBytes = "Passwordpasswod".getBytes();
        byte[] ivBytes = new byte[10];
        new java.util.Random().nextBytes(ivBytes);


        // wrap key data in Key/IV specs to pass to cipher
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        // create the cipher with the algorithm you choose
        // see javadoc for Cipher class for more info, e.g.
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");


        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        byte[] encrypted= new byte[cipher.getOutputSize(input.length)];
        int enc_len = cipher.update(input, 0, input.length, encrypted, 0);
        enc_len += cipher.doFinal(encrypted, enc_len);

        System.out.println(Arrays.toString(encrypted));

        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        byte[] decrypted = new byte[cipher.getOutputSize(enc_len)];
        int dec_len = cipher.update(encrypted, 0, enc_len, decrypted, 0);
        dec_len += cipher.doFinal(decrypted, dec_len);

        System.out.println(Arrays.toString(decrypted));*/

        /* Derive the key, given password and salt. */

        char[] password = "mynAme".toCharArray();
        byte[] salt = new byte[8];
        new java.util.Random().nextBytes(salt);
        String text = "Yo hi what's uo!";

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(password, salt, 65536, 128);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
        /* Encrypt the message. */
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        AlgorithmParameters params = cipher.getParameters();
        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
        byte[] ciphertext = cipher.doFinal(text.getBytes("UTF-8"));

        /* Decrypt the message, given derived key and initialization vector. */
        Cipher cipher2 = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher2.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
        String plaintext = new String(cipher2.doFinal(ciphertext), "UTF-8");
        System.out.println(plaintext);
    }
}
