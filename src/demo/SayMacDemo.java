package com.almasb.java.framework.demo;

import java.io.IOException;

import com.almasb.common.net.ClientPacketParser;
import com.almasb.common.net.DataPacket;
import com.almasb.common.net.UDPServer;

/**
 * @author Almas Baimagambetov
 * @version 1.0
 *
 */
public class SayMacDemo {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        final Runtime rt = Runtime.getRuntime();

        @SuppressWarnings("unused")
        UDPServer server = new UDPServer(55555, new ClientPacketParser() {

            @Override
            public void parseClientPacket(DataPacket packet) {

                try {
                    rt.exec("say " + packet.stringData);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
    }

}
