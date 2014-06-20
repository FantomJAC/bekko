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
package com.valleycampus.xbee.api.s2;

import com.valleycampus.zigbee.io.FrameBuffer;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class ZigBeeTransmitRequest extends XBeeAddressingRequest {

    private byte broadcastRadius;
    private byte options;
    private byte[] packet;
    
    public ZigBeeTransmitRequest() {
        setFrameType(API_ZB_TRANSMIT_REQ);
    }
    
    public void pull(FrameBuffer frameBuffer) {
        super.pull(frameBuffer);
        frameBuffer.putInt8(broadcastRadius);
        frameBuffer.putInt8(options);
        if (packet != null) {
            frameBuffer.put(packet);
        }
    }

    public int quote() {
        int q = super.quote() + 2;
        if (packet != null) {
            q += packet.length;
        }
        return q;
    }

    /**
     * @return the broadcastRadius
     */
    public byte getBroadcastRadius() {
        return broadcastRadius;
    }

    /**
     * @param broadcastRadius the broadcastRadius to set
     */
    public void setBroadcastRadius(byte broadcastRadius) {
        this.broadcastRadius = broadcastRadius;
    }

    /**
     * @return the options
     */
    public byte getOptions() {
        return options;
    }

    /**
     * @param options the options to set
     */
    public void setOptions(byte options) {
        this.options = options;
    }

    /**
     * @return the packet
     */
    public byte[] getPacket() {
        return packet;
    }

    /**
     * @param packet the packet to set
     */
    public void setPacket(byte[] packet) {
        this.packet = packet;
    }
}
