/*
 * Copyright (C) 2014 Valley Campus Japan, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.valleycampus.zigbee.io;

/**
 *
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public class FrameBuffer extends ByteBuffer {

    public static final byte TRUE = 0x01;
    public static final byte FALSE = 0x00;
    public static final int BO_LITTLE_ENDIAN = 0;
    public static final int BO_BIG_ENDIAN = 1;
    private int byteOrder;

    public FrameBuffer(byte[] buffer, int offset, int length) {
        this(BO_LITTLE_ENDIAN, buffer, offset, length);
    }
    
    public FrameBuffer(int byteOrder, byte[] buffer, int offset, int length) {
        super(buffer, offset, length);
        this.byteOrder = byteOrder;
    }

    public FrameBuffer(byte[] buffer) {
        this(BO_LITTLE_ENDIAN, buffer);
    }
    
    public FrameBuffer(int byteOrder,byte[] buffer) {
        super(buffer);
        this.byteOrder = byteOrder;
    }

    public int getByteOrder() {
        return byteOrder;
    }

    public FrameBuffer setByteOrder(int byteOrder) {
        this.byteOrder = byteOrder;
        return this;
    }

    public FrameBuffer putInt8(int i) {
        put((byte) (i & 0xFF));
        return this;
    }

    public FrameBuffer putInt16(int i) {
        if (byteOrder == BO_LITTLE_ENDIAN) {
            put((byte) (i & 0xFF));
            put((byte) ((i >> 8) & 0xFF));
        } else {
            put((byte) ((i >> 8) & 0xFF));
            put((byte) (i & 0xFF));
        }
        return this;
    }

    public FrameBuffer putInt32(int i) {
        if (byteOrder == BO_LITTLE_ENDIAN) {
            put((byte) (i & 0xFF));
            put((byte) ((i >> 8) & 0xFF));
            put((byte) ((i >> 16) & 0xFF));
            put((byte) ((i >> 24) & 0xFF));
        } else {
            put((byte) ((i >> 24) & 0xFF));
            put((byte) ((i >> 16) & 0xFF));
            put((byte) ((i >> 8) & 0xFF));
            put((byte) (i & 0xFF));
        }
        return this;
    }
    
    public FrameBuffer putBoolean(boolean b) {
        put(b ? TRUE : FALSE);
        return this;
    }
    
    public FrameBuffer putInt(byte[] src, int srcOff, int srcLen) {
        if (byteOrder == BO_LITTLE_ENDIAN) {
            for (int i = (srcOff + srcLen) - 1; i >= 0; i--) {
                super.put(src[i]);
            }
        } else {
            super.put(src, srcOff, srcLen);
        }
        return this;
    }
    
    public FrameBuffer putBinary(byte[] src, int srcOff, int srcLen) {
        super.put(src, srcOff, srcLen);
        return this;
    }

    public int getInt8() {
        return get() & 0xFF;
    }

    public byte getByte() {
        return get();
    }

    public int getInt16() {
        int s = 0;
        if (byteOrder == BO_LITTLE_ENDIAN) {
            s |= (get() & 0xFF);
            s |= (get() & 0xFF) << 8;
        } else {
            s |= (get() & 0xFF) << 8;
            s |= (get() & 0xFF);
        }
        return s;
    }

    public short getShort() {
        return (short) getInt16();
    }

    public int getInt32() {
        int s = 0;
        if (byteOrder == BO_LITTLE_ENDIAN) {
            s |= (get() & 0xFF);
            s |= (get() & 0xFF) << 8;
            s |= (get() & 0xFF) << 16;
            s |= (get() & 0xFF) << 24;
        } else {
            s |= (get() & 0xFF) << 24;
            s |= (get() & 0xFF) << 16;
            s |= (get() & 0xFF) << 8;
            s |= (get() & 0xFF);
        }
        return s;
    }

    public FrameBuffer putInt64(long l) {
        if (byteOrder == BO_LITTLE_ENDIAN) {
            put(ByteUtil.LITTLE_ENDIAN.toByteArray(l, ByteUtil.INT_64_SIZE));
        } else {
            put(ByteUtil.BIG_ENDIAN.toByteArray(l, ByteUtil.INT_64_SIZE));
        }
        return this;
    }

    public long getInt64() {
        byte[] b = new byte[ByteUtil.INT_64_SIZE];
        get(b);
        if (byteOrder == BO_LITTLE_ENDIAN) {
            return ByteUtil.LITTLE_ENDIAN.toInt64(b, 0);
        } else {
            return ByteUtil.BIG_ENDIAN.toInt64(b, 0);
        }
    }
    
    public boolean getBoolean() {
        return (get() == TRUE);
    }
    
    public FrameBuffer getInt(byte[] dst, int dstOff, int dstLen) {
        if (byteOrder == BO_LITTLE_ENDIAN) {
            for (int i = (dstOff + dstLen) - 1; i >= 0; i--) {
                dst[i] = super.get();
            }
        } else {
            super.get(dst, dstOff, dstLen);
        }
        return this;
    }
    
    public FrameBuffer getBinary(byte[] dst, int dstOff, int dstLen) {
        super.get(dst, dstOff, dstLen);
        return this;
    }
    
    public byte[] getIntAsByteArray(int octets) {
        byte[] dst = new byte[octets];
        getInt(dst, 0, octets);
        return dst;
    }
    
    public byte[] getBinaryAsByteArray(int octets) {
        return super.getByteArray(octets);
    }
}
