/*
 * Copyright (C) 2012 Valley Campus Japan, Inc.
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
package com.valleycampus.xbee.xmodem;

import com.valleycampus.zigbee.io.Frame;
import com.valleycampus.zigbee.io.FrameBuffer;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class XBlock implements Frame {

    public static final byte CTRL_Z = 0x1A;
    public static final int DEFAULT_DATA_SIZE = 128;
    public static final int EXTENDED_DATA_SIZE = 1024;
    private int dataSize;
    private int sequence;
    private byte[] data;
    
    public XBlock(boolean extended) {
        dataSize = extended ? EXTENDED_DATA_SIZE : DEFAULT_DATA_SIZE;
    }

    public void pull(FrameBuffer fb) {
        fb.putInt8(sequence);
        fb.putInt8(0xFF - sequence & 0xFF);
        int actualDataSize = data.length;
        if (actualDataSize > dataSize) {
            actualDataSize = dataSize;
        }
        for (int i = 0; i < actualDataSize; i++) {
           fb.put(data[i]);
        }
        for (int i = 0; i < (dataSize - actualDataSize); i++) {
           fb.put(CTRL_Z);
        }
    }

    public int quote() {
        return 2 + dataSize;
    }

    public void drain(FrameBuffer fb) {
        this.sequence = fb.getInt8();
        int seqSub = fb.getInt8();
        if (((sequence & seqSub) & 0xFF) != 0) {
            // TODO: Invalid
        }
        data = new byte[dataSize];
        fb.get(data);
    }
    
    /**
     * @return the dataSize
     */
    public int getDataSize() {
        return dataSize;
    }

    /**
     * @param dataSize the dataSize to set
     */
    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    } 

    /**
     * @return the sequence
     */
    public int getSequence() {
        return sequence;
    }

    /**
     * @param sequence the sequence to set
     */
    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    /**
     * @return the data
     */
    public byte[] getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(byte[] data) {
        this.data = data;
    }
}
