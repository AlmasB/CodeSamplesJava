package com.almasb.common.net;

/**
 * Convenience class to parse only packets that came from server
 *
 */
public abstract class ServerPacketParser implements DataPacketParser {
    @Override
    public void parseClientPacket(DataPacket packet) {}
}
