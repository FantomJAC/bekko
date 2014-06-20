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
public class ByteBuffer extends Buffer {

    private int offset;
    private byte[] buffer;

    public ByteBuffer(byte[] buffer, int offset, int length) {
        super(length);
        this.buffer = buffer;
        this.offset = offset;
    }

    public ByteBuffer(byte[] buffer) {
        this(buffer, 0, buffer.length);
    }
    
    public String toString() {
        return ByteUtil.toString(buffer, offset, getPosition());
    }

    public int getOffset() {
        return offset;
    }

    public byte[] getRawArray() {
        return buffer;
    }
    
    public ByteBuffer extend(int newSize) {
        byte[] newArray = new byte[newSize];
        System.arraycopy(buffer, offset, newArray, 0, getCapacity());
        offset = 0;
        buffer = newArray;
        return this;
    }

    public ByteBuffer put(byte src) {
        buffer[offset + getPosition()] = src;
        skip(1);
        return this;
    }

    public ByteBuffer put(byte[] src) {
        System.arraycopy(src, offset, buffer, offset + getPosition(), src.length);
        skip(src.length);
        return this;
    }

    public ByteBuffer put(byte[] src, int srcOff, int srcLen) {
        System.arraycopy(src, srcOff, buffer, offset + getPosition(), srcLen);
        skip(srcLen);
        return this;
    }

    public ByteBuffer clean(int length) {
        for (int i = 0; i < length; i++) {
            put((byte) 0);
        }
        return this;
    }
    
    public byte peek() {
        if (getRemaining() < 1) {
            throw new BufferUnderflowException();
        }
        return buffer[offset + getPosition()];
    }

    public byte get() {
        if (getRemaining() < 1) {
            throw new BufferUnderflowException();
        }
        byte b = buffer[offset + getPosition()];
        skip(1);
        return b;
    }

    public ByteBuffer get(byte[] dst) {
        if (getRemaining() < dst.length) {
            throw new BufferUnderflowException();
        }
        System.arraycopy(buffer, offset + getPosition(), dst, 0, dst.length);
        skip(dst.length);
        return this;
    }

    public ByteBuffer get(byte[] dst, int dstOff, int dstLen) {
        if (getRemaining() < dstLen) {
            throw new BufferUnderflowException();
        }
        System.arraycopy(buffer, offset + getPosition(), dst, dstOff, dstLen);
        skip(dstLen);
        return this;
    }
    
    public byte[] getByteArray(int octets) {
        byte[] dst = new byte[octets];
        get(dst);
        return dst;
    }
}
