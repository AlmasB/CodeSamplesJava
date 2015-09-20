package com.almasb.common.net;

/**
 * Applications using {@code SocketConnection} and its subclasses will need a
 * certain class to process the data received by server/client
 *
 * The methods will be automatically called when a packet was received
 *
 * The packet's signature will identify the owner (server or client)
 *
 * @author Almas
 * @version 1.1
 *
 *          v 1.1 - we now send {@code DataPacket} instead of simple
 *          {@code String}
 */
public interface DataPacketParser {

    /**
     * Process a packet which came from server
     *
     * @param packet
     *            the received packet
     */
    public void parseServerPacket(DataPacket packet);

    /**
     * Process a packet which came from client
     *
     * @param packet
     *            the received packet
     */
    public void parseClientPacket(DataPacket packet);
}
