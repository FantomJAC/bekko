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
package com.valleycampus.xbee.api;

import com.valleycampus.zigbee.io.Frame;
import com.valleycampus.zigbee.io.FrameBuffer;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class XBeeFrame implements Frame {
    
    public static final byte FRAME_DELIMITER = 0x7E;
    
    // Common Frames
    public static final byte API_AT_COMMAND = (byte) 0x08;
    public static final byte API_AT_COMMAND_QPV = (byte) 0x9;
    public static final byte API_AT_COMMAND_RESP = (byte) 0x88;
    public static final byte API_MODEM_STATUS = (byte) 0x8A;
    
    // S1(S6) Frames
    public static final byte API_TX_REQ_64 = (byte) 0x00;
    public static final byte API_RX_PACKET_64 = (byte) 0x80;
    public static final byte API_TX_REQ_16 = (byte) 0x01;
    public static final byte API_RX_PACKET_16 = (byte) 0x81;
    public static final byte API_TX_STATUS = (byte) 0x89;
    
    // S2/S1(DigiMesh)/S5/S8/S3B(900HP) Frames
    public static final byte API_REMOTE_COMMAND_REQ = (byte) 0x17;
    public static final byte API_REMOTE_COMMAND_RESP = (byte) 0x97;
    public static final byte API_ZB_TRANSMIT_REQ = (byte) 0x10;
    public static final byte API_EXPLICIT_ADDR_ZB_CMD_FRM = (byte) 0x11;
    public static final byte API_ZB_TRANSMIT_STATUS = (byte) 0x8B;
    public static final byte API_ZB_RECEIVE_PACKET = (byte) 0x90;
    public static final byte API_ZB_EXPLICIT_RX_IND = (byte) 0x91;
    public static final byte API_NODE_ID_IND = (byte) 0x95;
    
    // S2 Frames
    public static final byte API_ZB_IOD_SAMPLE_RX_IND = (byte) 0x92;
    public static final byte API_XB_SENSOR_READ_IND = (byte) 0x94;
    public static final byte API_OTA_FIRM_UPDATE_STATUS = (byte) 0xA0;
    public static final byte API_CREATE_SOURCE_ROUTE = (byte) 0x21;
    public static final byte API_ROUTE_REC_IND = (byte) 0xA1;
    public static final byte API_MOR_ROUTE_REQUEST_IND = (byte) 0xA3;
    // S2-SE Frames
    public static final byte API_ZB_DEV_AUTH_IND = (byte) 0xA2;
    public static final byte API_ZB_REGIST_JOINING_DEV = (byte) 0x24;
    public static final byte API_ZB_REGIST_JOINING_DEV_STAT = (byte) 0xA4;
    
    // S6 Frames
    public static final byte API_TX_REQ_IP_V4 = (byte) 0x20;
    public static final byte API_RX_PACKET_IP_V4 = (byte) 0xB0;
    
    // S8/S3B(900HP) Frame
    public static final byte API_ROUTE_INFO_PACKET = (byte) 0x8D;
    public static final byte API_AGGREGATE_ADDR_UPDATE = (byte) 0x8E;
    
    private byte frameType;
    
    public void pull(FrameBuffer frameBuffer) {
        frameBuffer.setByteOrder(FrameBuffer.BO_BIG_ENDIAN);
        frameBuffer.putInt8(frameType);
    }
    
    public int quote() {
        return 1;
    }

    public void drain(FrameBuffer frameBuffer) {
        frameBuffer.setByteOrder(FrameBuffer.BO_BIG_ENDIAN);
        frameType = frameBuffer.getByte();
    }
    
    /**
     * @return the frameType
     */
    public byte getFrameType() {
        return frameType;
    }

    /**
     * @param frameType the frameType to set
     */
    public void setFrameType(byte frameType) {
        this.frameType = frameType;
    }
}
