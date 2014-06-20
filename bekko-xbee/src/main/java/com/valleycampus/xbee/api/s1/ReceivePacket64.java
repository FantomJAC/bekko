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
package com.valleycampus.xbee.api.s1;

import com.valleycampus.zigbee.io.FrameBuffer;
import com.valleycampus.xbee.api.XBeeResponse;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class ReceivePacket64 extends XBeeResponse {

    private long address64;
    private byte rssi;
    private byte receiveOptions;
    private byte[] packet;

    public void drain(FrameBuffer frameBuffer) {
        super.drain(frameBuffer);
        address64 = frameBuffer.getInt64();
        rssi = frameBuffer.getByte();
        receiveOptions = frameBuffer.getByte();
        if (frameBuffer.getRemaining() > 0) {
            packet = frameBuffer.getByteArray(frameBuffer.getRemaining());
        }
    }

    /**
     * @return the address64
     */
    public long getAddress64() {
        return address64;
    }

    /**
     * @return the RSSI
     */
    public byte getRSSI() {
        return rssi;
    }

    /**
     * @return the receiveOptions
     */
    public byte getReceiveOptions() {
        return receiveOptions;
    }

    /**
     * @return the packet
     */
    public byte[] getPacket() {
        return packet;
    }
}
