package com.almasb.common.net;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.URL;

/**
 * General class for a socket connection
 *
 * TODO: for all subclasses - proper close()
 *
 * @author Almas
 * @version 1.4
 *
 *          v 1.1 - added server and client signatures
 *          v 1.2 - added parser
 *          v 1.3 - send(String) replaced by more general send(DataPacket)
 *          v 1.4 - added object byte info getters
 */
public abstract class SocketConnection {

    /**
     * A signature that should be included in the data packet to keep track
     * where the packet originated
     */
    protected static final String SERVER_SIGNATURE = "S",
            CLIENT_SIGNATURE = "C";

    protected static final DataPacket PING = new DataPacket("PING"),
            PONG = new DataPacket("PONG");

    /**
     * Irrespective to whether it's client or server it needs to have data
     * packet parser to parse responses, the user must specify how to parse the
     * data
     */
    protected DataPacketParser parser;

    /**
     * Default constructor with a parser
     *
     * Subclasses must perform the actual socket binding
     *
     * @param parser
     *            the packet parser
     */
    public SocketConnection(DataPacketParser parser) {
        this.parser = parser;
    }

    /**
     * Send {@code DataPacket} to remote computer whether it's server or client
     *
     * However when sending subclasses must specify (using appropriate
     * signature) where the packet originates (server or client)
     *
     * @param packet
     *            the packet to send
     * @throws IOException
     *             if any I/O occurs
     */
    public abstract void send(DataPacket packet) throws IOException;

    /**
     * Close all connections/sockets/streams associated with this connection and
     * release all resources
     *
     * @throws IOException
     */
    public abstract void close() throws IOException;

    /**
     *
     * @return the port of the machine this is being called on
     */
    public abstract int getLocalPort();

    /**
     * Retrieves external IP of the machine using a website for identifying IP
     *
     * Keep in mind that if there's no connection to the website or the website
     * structure has changed the method behaviour will be undefined
     *
     * @return external IP of the machine as String
     */
    public static String getExternalIP() {
        String ip = "";
        try {
            URL ipServer = new URL("http://www.whatsmyip.us/imgcode.php");
            InputStream is = ipServer.openStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = in.readLine()) != null) {
                if (line.contains("</textarea>")) {
                    ip = line.substring(0, line.indexOf("</textarea>"));
                    break;
                }
            }

            in.close();
            is.close();
        }
        catch (Exception e) {
            ip = "Not identified";
        }
        return ip;
    }

    /**
     * Calculate the size of the packet in bytes
     *
     * Can be used to then estimate the size of the buffer to
     * catch this packet
     *
     * @param packet
     *               the packet
     * @return
     *          the size in bytes
     */
    public static int calculatePacketSize(DataPacket packet) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutput oo = new ObjectOutputStream(baos);
            oo.writeObject(packet);
            oo.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return baos.toByteArray().length;
    }

    /**
     *
     * @param obj
     *              object MUST be java.io.Serializable
     * @return
     *          object converted to byte[]
     */
    public static byte[] toByteArray(Object obj) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutput oo = new ObjectOutputStream(baos);
            oo.writeObject(obj);
            oo.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }
}
