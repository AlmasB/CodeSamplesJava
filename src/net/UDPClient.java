package com.almasb.common.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.almasb.common.net.DataPacket.Signature;

/**
 * Easy API for udp client
 *
 * @author Almas
 * @version 1.1
 *
 *          v 1.1 - splitting server/client
 */
public class UDPClient extends UDPConnection {

    /**
     * Address of the server
     */
    private InetAddress remoteAddress;

    /**
     * Port of the server
     */
    private int remotePort;

    /**
     * Creates a client connection to server using UDP
     *
     * @param remoteIP
     * @param remotePort
     *
     * @throws SocketException
     * @throws UnknownHostException
     */
    public UDPClient(String remoteIP, int remotePort, DataPacketParser parser)
            throws IOException {
        super(0, parser);
        remoteAddress = InetAddress.getByName(remoteIP);
        this.remotePort = remotePort;
        send(PING);
    }

    @Override
    public void send(DataPacket packet) throws IOException {
        packet.sign(Signature.CLIENT);
        send(packetToByteArray(packet), remoteAddress, remotePort);
    }

    /**
     * Retrieve address of the server
     *
     * @return internet address of the server this client is connected to
     */
    public InetAddress getRemoteAddress() {
        return remoteAddress;
    }

    /**
     * Retrieve port of the server
     *
     * @return port of the server this client is connected to
     */
    public int getRemotePort() {
        return remotePort;
    }

    /**
     *
     * @return
     *         {@code true} if client has been accepted by server,
     *         {@code false} otherwise
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     *
     * @return
     *          ping to server
     */
    public long getTimeTaken() {
        return timeTaken;
    }

    // TODO: proper name and DOC
    public long resetAC() {
        long tmp = ac;
        ac = 0;
        return tmp;
    }

    public long resetACSent() {
        long tmp = bytesSent;
        bytesSent = 0;
        return tmp;
    }

    //TODO: don't think there's a need for a separate method
    @Override
    public void send(DataPacket packet, String ip, int port) throws IOException {
        packet.sign(Signature.CLIENT);
        byte[] bytes = packetToByteArray(packet);
        send(bytes, InetAddress.getByName(ip), port);
    }
}
