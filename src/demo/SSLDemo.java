package com.almasb.java.framework.demo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class SSLDemo {

    public static void main(String[] args) throws Exception {
        SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory
                .getDefault();

        SSLServerSocket socket = (SSLServerSocket) factory
                .createServerSocket(55555);

        String[] cipherSuites = new String[] { "TLS_DH_anon_WITH_AES_256_CBC_SHA" };
        socket.setEnabledCipherSuites(cipherSuites);

        Socket sock = socket.accept();

        try (OutputStream os = sock.getOutputStream();
                InputStream is = sock.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        }

        sock.close();
    }
}
