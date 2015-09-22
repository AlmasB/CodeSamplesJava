package demo;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SSLTest {

    public static void main(String[] args) throws Exception {
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();

        SSLSocket socket = (SSLSocket) factory.createSocket("127.0.0.1", 55555);

        String[] cipherSuites = new String[] { "TLS_DH_anon_WITH_AES_128_CBC_SHA" };
        // you can change the cipher suites, there is quite a few available
        socket.setEnabledCipherSuites(cipherSuites);

        try (OutputStream os = socket.getOutputStream();
                InputStream is = socket.getInputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os))) {
            bw.write("Hello World!");
        }

        socket.close();
    }

}
