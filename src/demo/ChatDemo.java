package com.almasb.java.framework.demo;

import java.awt.Graphics2D;
import java.io.IOException;

import com.almasb.common.net.ClientPacketParser;
import com.almasb.common.net.DataPacket;
import com.almasb.common.net.ServerPacketParser;
import com.almasb.common.net.UDPClient;
import com.almasb.common.net.UDPConnection;
import com.almasb.common.net.UDPServer;
import com.almasb.common.parsing.InputParser;
import com.almasb.common.util.Out;
import com.almasb.java.ui.BasicWindow;
import com.almasb.java.ui.BufferedWindow;

public class ChatDemo {

    //"5.76.31.79"
    private static UDPConnection conn;

    public static void main(String[] args) throws Exception {

        boolean local = true;
        if (local) {
            conn = new UDPServer(55555, new ClientPacketParser() {
                @Override
                public void parseClientPacket(DataPacket packet) {
                    if (packet.stringData.startsWith("DATA:")) {
                        String data = packet.stringData.replace("DATA:", "");
                        Out.i(new String(packet.byteData), data);
                    }
                }
            });
        }
        else {
            conn = new UDPClient("5.76.31.79", 55555, new ServerPacketParser() {
                @Override
                public void parseServerPacket(DataPacket packet) {
                    if (packet.stringData.startsWith("DATA:")) {
                        String data = packet.stringData.replace("DATA:", "");
                        Out.i(new String(packet.byteData), data);
                    }
                }
            });
        }

        @SuppressWarnings("serial")
        BasicWindow window = new BufferedWindow(240, 200) {
            @Override
            protected void createPicture(Graphics2D g) {
                g.drawString("Some decent game", 50, 50);
            }
        };
        window.setVisible(true);

        window.getTerminal().setInputParser(new InputParser() {
            @Override
            public void parse(String input) {
                try {
                    if (conn != null)
                        conn.send(new DataPacket("DATA:" + input, System.getProperty("user.name").getBytes()));
                }
                catch (IOException e) {
                    Out.e("main", "Failed to send data packet", null, e);
                }
            }
        });

        window.getTerminal().setVisible(true);
    }


}
