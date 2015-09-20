package com.almasb.java.framework.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import com.almasb.common.util.Out;
import com.almasb.java.io.Resources;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class HTTPServerDemo {

        public static void main(String[] args) throws Exception {
            List<String> fileLines = new ArrayList<String>();
            String page = "Hellooooooo";
            for (String s : fileLines)
                page += s;


            HttpServer server = HttpServer.create(new InetSocketAddress(55555), 0);
            server.createContext("/index", new MyHandler(page));
            server.setExecutor(null);
            server.start();
        }

        static class MyHandler implements HttpHandler {
            private String response;

            public MyHandler(String s) {
                response = s;
            }

            @Override
            public void handle(HttpExchange t) throws IOException {
//                Out.d("Protocol: " + t.getProtocol());
//                Out.d("Request method: " + t.getRequestMethod());
//                Out.d("Request URI" + t.getRequestURI().toString());

                InputStream is = t.getRequestBody();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
//                String result;
//                while ((result = br.readLine()) != null)
//                    Out.debug("Body: " + result);


                //Out.debug("Request Headers" + t.getRequestHeaders().toString());


                //String response = "Hello World";
                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }

}
