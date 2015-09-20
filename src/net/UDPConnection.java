package com.almasb.common.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.almasb.common.net.DataPacket.Signature;
import com.almasb.common.util.ZIPCompressor;
import com.almasb.common.util.Out;

/**
 * General udp connection
 *
 * @author Almas
 * @version 1.2
 *
 *          v 1.1 - more functionality, send(DataPacket)
 *          v 1.2 - can send packets to specific address
 */
public abstract class UDPConnection extends SocketConnection {

    /**
     * The socket of local end of the connection
     */
    protected final DatagramSocket localSocket;

    /**
     * Separate packet listener thread
     */
    protected final UDPConnectionThread conn;

    /**
     * Used for client, {@code true} if client is connected
     */
    protected boolean isConnected = false;

    /**
     * Used for manual packet signing when raw data is received
     */
    private boolean isServer = false;

    /**
     * Holds all connected addresses Key - not used at the moment, perhaps will
     * be used for targeted broadcast Value - full inet address, practically
     * same as SocketAddress
     */
    protected Map<String, FullInetAddress> addresses = Collections
            .synchronizedMap(new HashMap<String, FullInetAddress>());

    private ZIPCompressor compressor = new ZIPCompressor();

    public UDPConnection(int port, DataPacketParser parser)
            throws SocketException {
        super(parser);
        isServer = this.getClass().getSimpleName().equals("UDPServer");
        localSocket = port == 0 ? new DatagramSocket() : new DatagramSocket(port);
        conn = new UDPConnectionThread();
        new Thread(conn).start();
    }

    @Override
    public int getLocalPort() {
        return localSocket.getLocalPort();
    }

    @Override
    public void close() throws IOException {
        isConnected = false;
        conn.stop();
        localSocket.close();
    }

    //TODO: generic method for any object, maybe move to utils?
    //Note: all objects have to be java.io.Serializable
    protected static byte[] packetToByteArray(DataPacket packet) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutput oo = new ObjectOutputStream(baos);
            oo.writeObject(packet);
            oo.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    protected synchronized void send(byte[] data, InetAddress address, int port) throws IOException {
        byte[] compressed = compressor.compress(data);

        bytesSent += compressed.length;
        localSocket.send(new DatagramPacket(compressed, compressed.length, address, port));
    }

    public abstract void send(DataPacket packet, String ip, int port) throws IOException;

    /**
     * Sends raw data (byte[]) to given ip and port
     *
     * @param bytes
     * @param ip
     * @param port
     * @throws IOException
     */
    public void sendRawBytes(byte[] bytes, String ip, int port) throws IOException {
        send(bytes, InetAddress.getByName(ip), port);
    }

    class FullInetAddress {
        public final InetAddress address;
        public final int port;

        public FullInetAddress(InetAddress address, int port) {
            this.address = address;
            this.port = port;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj != null && obj instanceof FullInetAddress) {
                FullInetAddress other = (FullInetAddress) obj;
                return this.address.getHostAddress().equals(
                        other.address.getHostAddress())
                        && this.port == other.port;
            }
            return false;
        }
    }

    private int x = 0;

    protected long timeTaken = 0;
    protected long ac = 0;
    protected long bytesSent = 0;

    private synchronized String getNext() {
        return "" + x++;
    }

    class UDPConnectionThread implements Runnable {
        private boolean running = true;

        @Override
        public void run() {
            while (running) {
                try {
                    // 16 KB buffer
                    byte[] buf = new byte[16384];    // make it calculate stuff before and adjust when needed to avoid problems
                    DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
                    localSocket.receive(datagramPacket);
                    FullInetAddress address = new FullInetAddress(
                            datagramPacket.getAddress(),
                            datagramPacket.getPort());

                    byte[] decompressed = compressor.decompress(datagramPacket.getData());

                    DataPacket packet;

                    try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(decompressed))) {
                        packet = (DataPacket) ois.readObject();
                    }
                    catch (Exception e) {
                        // parse as raw data
                        packet = new DataPacket(decompressed);
                        // if server then received from client else from server
                        packet.sign(isServer ? Signature.CLIENT : Signature.SERVER);
                    }

                    packet.setIP(datagramPacket.getAddress().getHostAddress());
                    packet.setPort(datagramPacket.getPort());

                    ac += datagramPacket.getLength();

                    // measure ping
                    timeTaken = 2 * (System.currentTimeMillis() - packet.getTime());
                    // TODO: fix this bug causing high cpu usage
                    // send only on request perhaps
                    //send(new DataPacket("$" + timeTaken), packet.getIP(), packet.getPort());

                    if (packet.getSignature() == Signature.SERVER) {
                        if (packet.stringData.equals(PONG.stringData)) {
                            isConnected = true;
                        }
                        else {
                            parser.parseServerPacket(packet);
                        }
                    }
                    else if (packet.getSignature() == Signature.CLIENT) {

                        if (packet.stringData.equals(PING.stringData)) {
                            synchronized (addresses) {
                                if (!addresses.containsValue(address)) {
                                    addresses.put(getNext(), address);  // TODO: we don't need getNext(), just a list is fine maybe
                                }
                            }

                            send(PONG, packet.getIP(), packet.getPort());   // greet client with "PONG"
                        }
                        else {
                            parser.parseClientPacket(packet);
                        }
                    }
                }
                catch (IOException e) {
                    if (running) {
                        Out.e("run", "Exception raised during receiving packet", this, e);
                        stop();
                    }
                }
            }
        }

        public void stop() {
            running = false;
        }
    }
}
