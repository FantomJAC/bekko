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
package com.valleycampus.zigbee.zdp.command;

import com.valleycampus.zigbee.IEEEAddress;
import com.valleycampus.zigbee.NetworkAddress;
import com.valleycampus.zigbee.ZigBeeAddress;
import com.valleycampus.zigbee.io.ByteUtil;
import com.valleycampus.zigbee.io.FrameBuffer;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class AddrReq implements ZDPCommand {
 
    public static final int REQ_NWK = 0;
    public static final int REQ_IEEE = 1;
    public static final byte REQUEST_TYPE_SINGLE = 0x00;
    public static final byte REQUEST_TYPE_EXTENDED = 0x01;
    
    private int request;
    private ZigBeeAddress address;
    private byte requestType;
    private int startIndex;
    
    public AddrReq(int request) {
        this.request = request;
    }

    public void pull(FrameBuffer frameBuffer) {
        if (request == REQ_NWK) {
            frameBuffer.putInt64(((IEEEAddress) address).toLong());
        } else {
            frameBuffer.putInt16(((NetworkAddress) address).toShort());
        }
        frameBuffer.put(requestType);
        frameBuffer.putInt8(startIndex);
    }

    public int quote() {
        int q = ((request == REQ_NWK) ?
                ByteUtil.INT_64_SIZE :
                ByteUtil.INT_16_SIZE)
                + ByteUtil.BYTE_SIZE + ByteUtil.INT_8_SIZE;
        return q;
    }

    public void drain(FrameBuffer frameBuffer) {
        if (request == REQ_NWK) {
            address = IEEEAddress.getByAddress(frameBuffer.getInt64());
        } else {
            address = NetworkAddress.getByAddress(frameBuffer.getShort());
        }
        requestType = frameBuffer.get();
        startIndex = frameBuffer.getInt8();
    }

    /**
     * @return the address
     */
    public ZigBeeAddress getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(ZigBeeAddress address) {
        this.address = address;
    }

    /**
     * @return the requestType
     */
    public byte getRequestType() {
        return requestType;
    }

    /**
     * @param requestType the requestType to set
     */
    public void setRequestType(byte requestType) {
        this.requestType = requestType;
    }

    /**
     * @return the startIndex
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * @param startIndex the startIndex to set
     */
    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }
}
