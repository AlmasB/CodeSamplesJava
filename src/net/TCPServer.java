package com.almasb.common.net;

import java.io.IOException;
import java.net.ServerSocket;

import com.almasb.common.net.DataPacket.Signature;


/**
 * Easy API to create a tcp server
 *
 * @author Almas
 * @version 1.0
 */
public class TCPServer extends TCPConnection {

    /**
     * Local socket to which this server is bound
     */
    private ServerSocket localServerSocket;

    /**
     * Constructs a tcp server with specified parser with a socket listening to
     * given port
     *
     * This ctor exec blocks until connection is made
     *
     * @param port
     *            server socket's port
     * @param parser
     *            data packet parser
     * @throws IOException
     */
    public TCPServer(int port, DataPacketParser parser) throws IOException {
        super(parser);
        localServerSocket = new ServerSocket(port);
        remoteSocket = localServerSocket.accept();
        init();
    }

    @Override
    public synchronized void send(DataPacket packet) throws IOException {
        packet.sign(Signature.SERVER);
        super.send(packet);
    }

    @Override
    public int getLocalPort() {
        return localServerSocket.getLocalPort();
    }

    @Override
    public void close() throws IOException {
        super.close();
        localServerSocket.close();
    }
}
