package com.ppnt.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class NumberConvertor {

    public static byte[] convertToByteArray(long unsignedInt) {
        byte[] byteArray = new byte[4];
        ByteBuffer buffer = ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt((int) (unsignedInt & 0xFFFFFFFF));
        return buffer.array();
    }
}
