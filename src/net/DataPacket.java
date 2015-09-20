package com.almasb.common.net;

import java.io.Serializable;

/**
 * Unified data packet used by both UDP and TCP connections
 * If a DataPacket was received it is guaranteed that no null
 * data will be present, i.e. there is no need to check for null in stringData or byteData, etc
 *
 * Note: an empty packet's size is currently 499 bytes
 * Note: excessive use of short names allows smaller size of the serialized version
 *
 *
 * @author Almas
 * @version 1.4
 *
 *          v 1.1 - added signature fromServer
 *          v 1.2 - simplified packet
 *                  construction, it is no longer required to specify who sent the packet
 *                  depending on end of connection, the sender will
 *                  sign the packet with appropriate signature
 *          v 1.3 - added objectData, now datapacket can carry serializable objects
 *                  fixed wrong javadoc comments
 *                  support for sending arrays
 *          v 1.4 - added "ip" and port fields and accessors to hold ip of the sender
 *                  if you are the sender ip is empty string and port 0
 */
public class DataPacket implements Serializable {

    /**
     * Serializable
     */
    private static final long serialVersionUID = 3256232093228720518L;

    enum Signature {
        NONE, SERVER, CLIENT
    }

    /**
     * Identifies who sent the packet
     */
    private Signature signature = Signature.NONE;

    /**
     * Holds value in milliseconds when the packet was sent
     * To be precise when packet was signed
     * It is up to the user to sign just before sending
     *
     */
    private long timeSent = 0;

    /**
     * Shows where packet came from
     * The packet that is being sent has empty string instead of IP
     */
    private transient String ip = "";

    /**
     * Shows where packet came from
     * The packet that is being sent has 0 as port
     */
    private transient int port = 0;

    /**
     * Data carried in a String, separate from {@link #byteData} Useful for
     * sending text
     */
    public final String stringData;

    /**
     * Data carried in a byte array, separate from {@link #stringData} Useful
     * for sending files or raw bytes
     */
    public final byte[] byteData;

    /**
     * Data carried as an object, the receiver MUST check if it's the type
     * they are expecting, for example use "instanceof"
     *
     * The object MUST also be a serializable object otherwise exceptions
     * might be thrown
     */
    public final Serializable objectData;

    /**
     * Data carried as an array of objects, the receiver MUST check if it's the type
     * they are expecting, for example use "instanceof"
     *
     * The object MUST also be a serializable object otherwise exceptions
     * might be thrown
     */
    public final Serializable[] multipleObjectData;

    /**
     * Constructs a packet with given type which carries byte data
     *
     * @param byteData
     */
    public DataPacket(byte[] byteData) {
        this("", byteData, new S(), new Serializable[] {});
    }

    /**
     * Constructs a packet with given type which carries string data
     *
     * @param stringData
     */
    public DataPacket(String stringData) {
        this(stringData, new byte[] {}, new S(), new Serializable[] {});
    }

    /**
     * Constructs a packet with given object data
     *
     * @param objectData
     */
    public DataPacket(Serializable objectData) {
        this("", new byte[] {}, objectData, new Serializable[] {});
    }

    /**
     * Constructs a packet with given object data
     *
     * @param multipleObjectsData
     */
    public DataPacket(Serializable[] multipleObjectData) {
        this("", new byte[] {}, new S(), multipleObjectData);
    }

    /**
     * Constructs a packet with given type which carries byte data,
     * string data and object data
     *
     * @param stringData
     * @param byteData
     * @param objectData
     * @param multipleObjectData
     */
    public DataPacket(String stringData, byte[] byteData, Serializable objectData, Serializable[] multipleObjectData) {
        this.stringData = stringData;
        this.byteData = byteData;
        this.objectData = objectData;
        this.multipleObjectData = multipleObjectData;
    }

    /**
     * Constructs a packet with given type which carries byte data,
     * string data and object data
     *
     * @param stringData
     * @param byteData
     */
    public DataPacket(String stringData, byte[] byteData) {
        this(stringData, byteData, new S(), new Serializable[] {});
    }

    /**
     * Signs the packet
     *
     * @param signature
     *            either server or client
     */
    /*package-private*/ void sign(Signature signature) {
        this.signature = signature;
        timeSent = System.currentTimeMillis();
    }

    /**
     * Get packet signature
     *
     * @return packet signature
     */
    /*package-private*/ Signature getSignature() {
        return signature;
    }

    /**
     *
     * @return
     *          ip of the packet origin,
     *          empty string if the packet is originated on this machine
     */
    public String getIP() {
        return ip;
    }

    /**
     * Set ip, will be done automatically
     * upon receiving
     *
     * @param ip
     */
    public void setIP(String ip) {
        this.ip = ip;
    }

    /**
     *
     * @return
     *          port of the packet origin,
     *          0 if the packet is originated on this machine
     */
    public int getPort() {
        return port;
    }

    /**
     * Set port, will be done automatically
     * upon receiving
     *
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    public long getTime() {
        return timeSent;
    }

    /**
     * Serializable Object, just a dummy object that implements java.io.Serializable
     *
     * @author Almas Baimagambetov (ab607@uni.brighton.ac.uk)
     * @version 1.0
     *
     */
    static class S implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 8229037616837666038L;

    }
}
