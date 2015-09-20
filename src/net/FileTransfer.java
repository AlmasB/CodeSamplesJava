package com.almasb.common.net;

import java.io.*;
import java.util.Arrays;

/**
 * Allows easy file transfer over Internet and local network
 *
 * @author Almas Baimagambetov (ab607@uni.brighton.ac.uk)
 * @version 1.0
 *
 */
public class FileTransfer {

    private static final String FT_NAME = "$FT_NAME$";
    private static final String FT_END = "$FT_END$";
    private static final String FT_CLOSE = "$FT_CLOSE$";

    private TCPConnection tcpConn;

    private FileInputStream iStream;

    private FileOutputStream oStream;

    /**
     * "server" ctor
     *
     * @param port
     * @throws IOException
     */
    public FileTransfer(int port) throws IOException {
        tcpConn = new TCPServer(port, new DataParser());
    }

    /**
     * "client" ctor
     *
     * @param ip
     * @param port
     * @throws IOException
     */
    public FileTransfer(String ip, int port) throws IOException {
        tcpConn = new TCPClient(ip, port, new DataParser());
    }

    public void send(String fileName) throws IOException {
        tcpConn.send(new DataPacket(FT_NAME + fileName));

        iStream = new FileInputStream(fileName);
        byte[] buffer = new byte[4096];
        int bytesRead = 0;

        while ((bytesRead = iStream.read(buffer)) != -1) {
            tcpConn.send(new DataPacket(Arrays.copyOf(buffer, bytesRead)));
        }

        tcpConn.send(new DataPacket(FT_END + fileName));
        if (iStream != null)
            iStream.close();
    }

    public void send(String fileName, byte[] data) throws IOException {
        tcpConn.send(new DataPacket(FT_NAME + fileName));
        tcpConn.send(new DataPacket(data));
        tcpConn.send(new DataPacket(FT_END + fileName));
    }

    public void closeConnection() throws IOException {
        tcpConn.send(new DataPacket(FT_CLOSE));
        close();
    }

    private void close() throws IOException {
        if (tcpConn != null)
            tcpConn.close();
        if (iStream != null)
            iStream.close();
        if (oStream != null)
            oStream.close();
    }

    class DataParser implements DataPacketParser {
        @Override
        public void parseServerPacket(DataPacket packet) {
            try {
                recv(packet);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void parseClientPacket(DataPacket packet) {
            try {
                recv(packet);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void recv(DataPacket packet) throws IOException {
            if (packet.stringData.startsWith(FT_CLOSE)) {
                close();
            }
            else if (packet.stringData.startsWith(FT_NAME)) {
                String fileName = packet.stringData.replace(FT_NAME, "");
                if (fileName.contains("/")) {
                    fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                }

                if (fileName.contains("\\")) {
                    fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
                }

                /*if (fileName.contains("/")) {
                    File parentDirs = new File(fileName.substring(0, fileName.lastIndexOf("/")) + "/");
                    if (!parentDirs.exists()) {
                        parentDirs.mkdirs();
                    }
                }*/

                oStream = new FileOutputStream(fileName);
            }
            else if (packet.stringData.startsWith(FT_END)) {
                if (oStream != null)
                    oStream.close();
            }
            else {
                // byte data
                if (oStream != null) {
                    oStream.write(packet.byteData);
                }
            }
        }
    }
}
