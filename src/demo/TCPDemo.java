package com.almasb.java.framework.demo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class TCPDemo {

    public static void main(String[] args) throws Exception {

        try (ServerSocket server = new ServerSocket(55555);
                Socket client = server.accept();
                OutputStream os = client.getOutputStream();
                InputStream is = client.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            client.setTcpNoDelay(true);
            String line;
            while ((line = reader.readLine()) != null)
                System.out.println(line);
            //os.write("Hello from Java".getBytes(Charset.forName("UTF-8")));

        }
    }
}
