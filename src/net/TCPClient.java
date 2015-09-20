package com.almasb.common.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.almasb.common.net.DataPacket.Signature;

/**
 * Easy API for creating a tcp client
 *
 * @author Almas
 * @version 1.0
 */
public class TCPClient extends TCPConnection {

    /**
     * Address of the server
     */
    private InetAddress remoteAddress;

    /**
     * Port of the server
     */
    private int remotePort;

    /**
     * Creates a client connection to server using TCP
     *
     * @param remoteIP
     *            textual representation of server IP address, if the address is
     *            in the local network you can also use host name of the server
     *            computer
     * @param remotePort
     *            the server port to which to connect
     *
     * @throws SocketException
     * @throws UnknownHostException
     * @throws IOException
     */
    public TCPClient(String remoteIP, int remotePort, DataPacketParser parser)
            throws SocketException, UnknownHostException, IOException {
        super(parser);
        remoteAddress = InetAddress.getByName(remoteIP);
        this.remotePort = remotePort;
        remoteSocket = new Socket(remoteAddress, this.remotePort);
        init();
    }

    @Override
    public synchronized void send(DataPacket packet) throws IOException {
        packet.sign(Signature.CLIENT);
        super.send(packet);
    }

    /**
     * Not necessarily correct
     */
    @Override
    public int getLocalPort() {
        return remoteSocket.getLocalPort();
    }
}
