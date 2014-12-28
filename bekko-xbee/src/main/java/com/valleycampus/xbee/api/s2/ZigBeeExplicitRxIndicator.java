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
public class ZigBeeExplicitRxIndicator extends XBeeAddressingIndicator {

    public static final byte ACK = 0x01;
    public static final byte BROADCAST_PACKET = 0x02;
    public static final byte ENCRYPTED_APS_ENCRYPTION = 0x03;
    public static final byte SENT_FROM_ENDDEVICE = 0x04;
    private int sourceEndpoint;
    private int destinationEndpoint;
    private short clusterId;
    private short profileId;
    private byte receiveOptions;
    private byte[] payload;
    
    public void drain(FrameBuffer frameBuffer) {
        super.drain(frameBuffer);
        sourceEndpoint = frameBuffer.getInt8();
        destinationEndpoint = frameBuffer.getInt8();
        clusterId = frameBuffer.getShort();
        profileId = frameBuffer.getShort();
        receiveOptions = frameBuffer.getByte();
        if (frameBuffer.getRemaining() > 0) {
            payload = frameBuffer.getByteArray(frameBuffer.getRemaining());
        }
    }

    /**
     * @return the sourceEndpoint
     */
    public int getSourceEndpoint() {
        return sourceEndpoint;
    }

    /**
     * @return the destinationEndpoint
     */
    public int getDestinationEndpoint() {
        return destinationEndpoint;
    }

    /**
     * @return the clusterId
     */
    public short getClusterId() {
        return clusterId;
    }

    /**
     * @return the profileId
     */
    public short getProfileId() {
        return profileId;
    }

    /**
     * @return the receiveOptions
     */
    public byte getReceiveOptions() {
        return receiveOptions;
    }

    /**
     * @return the receivedData
     */
    public byte[] getPayload() {
        return payload;
    }
}
