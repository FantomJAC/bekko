/*
 * Copyright (C) 2013 Valley Campus Japan, Inc.
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
package com.valleycampus.xbee.api.s6;

import com.valleycampus.xbee.api.XBeeResponse;
import com.valleycampus.zigbee.io.FrameBuffer;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class ReceivePacketIPv4 extends XBeeResponse {
    
    private int address32;
    private int destinationPort;
    private int sourcePort;
    private byte protocol;
    private byte status;
    private byte[] data;

    public void drain(FrameBuffer frameBuffer) {
        super.drain(frameBuffer);
        address32 = frameBuffer.getInt32();
        destinationPort = frameBuffer.getInt16();
        sourcePort = frameBuffer.getInt16();
        protocol = frameBuffer.getByte();
        status = frameBuffer.getByte();
        if (frameBuffer.getRemaining() > 0) {
            data = frameBuffer.getByteArray(frameBuffer.getRemaining());
        }
    }
    
    /**
     * @return the address32
     */
    public int getAddress32() {
        return address32;
    }

    /**
     * @return the destinationPort
     */
    public int getDestinationPort() {
        return destinationPort;
    }

    /**
     * @return the sourcePort
     */
    public int getSourcePort() {
        return sourcePort;
    }

    /**
     * @return the protocol
     */
    public byte getProtocol() {
        return protocol;
    }

    /**
     * @return the status
     */
    public byte getStatus() {
        return status;
    }

    /**
     * @return the data
     */
    public byte[] getData() {
        return data;
    }
}
