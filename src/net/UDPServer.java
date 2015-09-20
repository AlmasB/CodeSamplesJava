package com.almasb.common.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.almasb.common.net.DataPacket.Signature;

/**
 * UDP using server for the host machine
 *
 * @author Almas
 * @version 1.1
 *
 *          v 1.1 - major refactoring, splitting server and client multi client
 *          support
 */
public class UDPServer extends UDPConnection {

    /**
     * Constructs server that uses UDP and starts listening to incoming
     * connections at port
     *
     * @param port
     * @param parser
     * @throws SocketException
     */
    public UDPServer(int port, DataPacketParser parser) throws SocketException {
        super(port, parser);
    }

    /**
     * Sends given data packet to all connected clients
     *
     * @param packet
     *              the data packet to send
     */
    @Override
    public void send(DataPacket packet) throws IOException {
        packet.sign(Signature.SERVER);
        byte[] bytes = packetToByteArray(packet);
        Set<String> set = addresses.keySet();
        synchronized (addresses) {
            for (String id : set) {
                FullInetAddress a = addresses.get(id);
                send(bytes, a.address, a.port);
            }
        }
    }

    public void sendRawBytes(byte[] bytes) throws IOException {
        Set<String> set = addresses.keySet();
        synchronized (addresses) {
            for (String id : set) {
                FullInetAddress a = addresses.get(id);
                send(bytes, a.address, a.port);
            }
        }
    }

    @Override
    public void send(DataPacket packet, String ip, int port) throws IOException {
        packet.sign(Signature.SERVER);
        byte[] bytes = packetToByteArray(packet);
        send(bytes, InetAddress.getByName(ip), port);
    }

    /**
     * -EXPERIMENTAL-
     *
     * Remove given address from the list
     * so that {@link #send(DataPacket)} will no longer
     * send packets to that address
     *
     * @param ip
     * @param port
     */
    public void removeAddress(String ip, int port) {
        InetAddress a = null;

        try {
            a = InetAddress.getByName(ip);
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }

        if (a != null) {
            synchronized (addresses) {
                Iterator<Entry<String, FullInetAddress> > it = addresses.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, FullInetAddress> pair = (Map.Entry<String, FullInetAddress>) it.next();
                    if (pair.getValue().equals(new FullInetAddress(a, port))) {
                        it.remove();
                        break;
                    }
                }
            }
        }
    }
}
