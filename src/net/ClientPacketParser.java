package com.almasb.common.net;

/**
 * Convenience class to parse only packets that came from client
 *
 */
public abstract class ClientPacketParser implements DataPacketParser {
    @Override
    public void parseServerPacket(DataPacket packet) {}
}
