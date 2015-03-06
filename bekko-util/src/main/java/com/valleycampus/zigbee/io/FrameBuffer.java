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
    
    private ByteUtil getByteUtil() {
        if (byteOrder == BO_LITTLE_ENDIAN) {
            return ByteUtil.LITTLE_ENDIAN;
        } else {
            return ByteUtil.BIG_ENDIAN;
        }
    }

    public int getByteOrder() {
        return byteOrder;
    }

    public FrameBuffer setByteOrder(int byteOrder) {
        this.byteOrder = byteOrder;
        return this;
    }

    public FrameBuffer putFrame(Frame frame) {
        frame.pull(this);
        return this;
    }
    
    public FrameBuffer putInt8(int i) {
        put((byte) (i & 0xFF));
        return this;
    }

    public FrameBuffer putInt16(int i) {
        put(getByteUtil().toByteArray(i, ByteUtil.INT_16_SIZE));
        return this;
    }

    public FrameBuffer putInt32(int i) {
        put(getByteUtil().toByteArray(i, ByteUtil.INT_32_SIZE));
        return this;
    }
    
    public FrameBuffer putBoolean(boolean b) {
        put(b ? TRUE : FALSE);
        return this;
    }
    
    public FrameBuffer getFrame(Frame frame) {
        frame.drain(this);
        return this;
    }

    public int getInt8() {
        return get() & 0xFF;
    }

    public byte getByte() {
        return get();
    }

    public int getInt16() {
        return getByteUtil().toInt16(getByteArray(ByteUtil.INT_16_SIZE), 0);
    }

    public short getShort() {
        return (short) getInt16();
    }

    public int getInt32() {
        return getByteUtil().toInt32(getByteArray(ByteUtil.INT_32_SIZE), 0);
    }

    public FrameBuffer putInt64(long l) {
        put(getByteUtil().toByteArray(l, ByteUtil.INT_64_SIZE));
        return this;
    }

    public long getInt64() {
        return getByteUtil().toInt64(getByteArray(ByteUtil.INT_64_SIZE), 0);
    }
    
    public boolean getBoolean() {
        return (get() == TRUE);
    }
}
