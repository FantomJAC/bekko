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

import com.valleycampus.xbee.api.XBeeFrameIdResponse;
import com.valleycampus.zigbee.io.FrameBuffer;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class ZigBeeTransmitStatus extends XBeeFrameIdResponse {

    // Delivery Status
    public static final byte STATUS_SUCCESS = 0x00;
    public static final byte STATUS_MAC_ACK_FAILURE = 0x01;
    public static final byte STATUS_CCA_FAILURE = 0x02;
    public static final byte STATUS_INVAILD_DESTINATION_ENDPOINT = 0x15;
    public static final byte STATUS_NETWORK_ACK_FAILURE = 0x21;
    public static final byte STATUS_NOT_JOINED_NETWORK = 0x22;
    public static final byte STATUS_SELF_ADDRESSED = 0x23;
    public static final byte STATUS_ADDRESS_NOT_FOUND = 0x24;
    public static final byte STATUS_ROUTE_NOT_FOUND = 0x25;
    public static final byte STATUS_BROADCAST_SOURCE_FAILED = 0x26;
    public static final byte STATUS_INVALID_BINDING_TABLE_INDEX = 0x2B;
    public static final byte STATUS_RESOURCE_ERROR_1 = 0x2C;
    public static final byte STATUS_ATTEMPTED_BROADCAST_APS = 0x2D;
    public static final byte STATUS_ATTEMPTED_UNICAST_APS = 0x2E; // EE = 0
    public static final byte STATUS_RESOURCE_ERROR_2 = 0x32;
    public static final byte STATUS_DATA_PAYLOAD_TOO_LARGE = 0x74;
    public static final byte STATUS_INDIRECT_MESSAGE_UNREQUEATED = 0x75;
    
    // Discovery Status
    public static final byte STATUS_NO_DISCOVERY = 0x00;
    public static final byte STATUS_ADDRESS_DISCOVERY = 0x01;
    public static final byte STATUS_ROUTE_DISCOVERY = 0x02;
    public static final byte STATUS_ADDRESS_AND_ROUTE = 0x03;
    public static final byte STATUS_EXTENDED_TIMEOUT = 0x04;
    
    private short address16;
    private int transmitRetryCount;
    private byte deliveryStatus;
    private byte discoveryStatus;
    
    public void drain(FrameBuffer frameBuffer) {
        super.drain(frameBuffer);
        address16 = frameBuffer.getShort();
        transmitRetryCount = frameBuffer.getInt8();
        deliveryStatus = frameBuffer.getByte();
        discoveryStatus = frameBuffer.getByte();
    }

    /**
     * @return the address16
     */
    public short getAddress16() {
        return address16;
    }

    /**
     * @return the transmitRetryCount
     */
    public int getTransmitRetryCount() {
        return transmitRetryCount;
    }

    /**
     * @return the deliveryStatus
     */
    public byte getDeliveryStatus() {
        return deliveryStatus;
    }

    /**
     * @return the discoveryStatus
     */
    public byte getDiscoveryStatus() {
        return discoveryStatus;
    }
}
