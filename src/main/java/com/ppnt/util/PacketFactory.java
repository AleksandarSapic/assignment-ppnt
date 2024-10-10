package com.ppnt.util;

import java.nio.ByteBuffer;

public class PacketFactory {

    private static final int DUMMY_PACKET_ID = 1;
    private static final int DUMMY_PACKET_SIZE = 16;
    private static final int CANCEL_PACKET_ID = 2;
    private static final int CANCEL_PACKET_SIZE = 12;


    public static byte[] createDummyPacket(long packetUniqueId, int delay) {
        ByteBuffer buffer = ByteBuffer.allocate(DUMMY_PACKET_SIZE);
        buffer.putInt(DUMMY_PACKET_ID);
        buffer.putInt(DUMMY_PACKET_SIZE);
        buffer.put(NumberConvertor.convertToByteArray(packetUniqueId));
        buffer.putInt(delay);
        return buffer.array();
    }

    public static byte[] createCancelPacket(long packetUniqueId) {
        ByteBuffer buffer = ByteBuffer.allocate(CANCEL_PACKET_SIZE);
        buffer.putInt(CANCEL_PACKET_ID);
        buffer.putInt(CANCEL_PACKET_SIZE);
        buffer.put(NumberConvertor.convertToByteArray(packetUniqueId));
        return buffer.array();
    }
}
