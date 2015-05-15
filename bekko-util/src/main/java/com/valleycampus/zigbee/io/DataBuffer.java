/*
 * Copyright (C) 2015 Valley Campus Japan, Inc.
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
public class DataBuffer extends ByteBuffer {

    public static final byte TRUE = 0x01;
    public static final byte FALSE = 0x00;
	private boolean littleEndian = true;
    
    protected DataBuffer(byte[] buffer, int offset, int length) {
        super(buffer, offset, length);
    }
	
	public static DataBuffer wrap(byte[] buffer) {
		return wrap(buffer, 0, buffer.length);
	}
	
	public static DataBuffer wrap(byte[] buffer, int offset, int length) {
		return new DataBuffer(buffer, offset, length);
	}
	
	public static DataBuffer wrap(byte[] buffer, int offset, int length, boolean littleEndian) {
		DataBuffer instance = new DataBuffer(buffer, offset, length);
		instance.setLittleEndian(littleEndian);
		return instance;
	}
	
	public static DataBuffer allocate(int capacity) {
		return new DataBuffer(new byte[capacity], 0, capacity);
	}
	
	public static DataBuffer allocate(int capacity, boolean littleEndian) {
		DataBuffer instance = new DataBuffer(new byte[capacity], 0, capacity);
		instance.setLittleEndian(littleEndian);
		return instance;
	}
    
    private ByteUtil getByteUtil() {
        if (littleEndian) {
            return ByteUtil.LITTLE_ENDIAN;
        } else {
            return ByteUtil.BIG_ENDIAN;
        }
    }

    public boolean isLittleEndian() {
        return littleEndian;
    }

    public DataBuffer setLittleEndian(boolean littleEndian) {
        this.littleEndian = littleEndian;
        return this;
    }

    public DataBuffer putDataFrame(DataFrame frame) {
        frame.push(this);
        return this;
    }
    
    public DataBuffer putInt8(int i) {
        put((byte) (i & 0xFF));
        return this;
    }

    public DataBuffer putInt16(int i) {
        put(getByteUtil().toByteArray(i, ByteUtil.INT_16_SIZE));
        return this;
    }

    public DataBuffer putInt32(int i) {
        put(getByteUtil().toByteArray(i, ByteUtil.INT_32_SIZE));
        return this;
    }
    
    public DataBuffer putBoolean(boolean b) {
        put(b ? TRUE : FALSE);
        return this;
    }
    
    public DataBuffer getDataFrame(DataFrame frame) {
        frame.pull(this);
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

    public DataBuffer putInt64(long l) {
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
