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
    
    public static final int DATA_SIZE = 128;
    public static final byte CTRL_Z = 0x1A;
    private byte sequence;
    private byte[] data;

    public void pull(FrameBuffer fb) {
        fb.put(sequence);
        fb.put((byte) (0xFF - sequence & 0xFF));
        int dataLength = data.length;
        if (dataLength > DATA_SIZE) {
            dataLength = DATA_SIZE;
        }
        for (int i = 0; i < dataLength; i++) {
           fb.put(data[i]);
        }
        for (int i = 0; i < (DATA_SIZE - dataLength); i++) {
           fb.put(CTRL_Z);
        }
    }

    public int quote() {
        return 2 + DATA_SIZE;
    }

    public void drain(FrameBuffer fb) {
        this.sequence = fb.get();
        byte seqSub = fb.get();
        if (((sequence & seqSub) & 0xFF) != 0) {
            return;
        }
        data = new byte[DATA_SIZE];
        fb.get(data);
        int cs = 0;
        for (int i = 0; i < DATA_SIZE; i++) {
           cs += data[i];
        }
    }

    /**
     * @return the sequence
     */
    public byte getSequence() {
        return sequence;
    }

    /**
     * @param sequence the sequence to set
     */
    public void setSequence(byte sequence) {
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
