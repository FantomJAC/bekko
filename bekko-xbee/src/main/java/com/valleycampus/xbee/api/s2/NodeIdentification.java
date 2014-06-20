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
package com.valleycampus.xbee.api.s2;

import com.valleycampus.zigbee.io.Frame;
import com.valleycampus.zigbee.io.FrameBuffer;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class NodeIdentification implements Frame {
    
    private short remoteAddress16;
    private long remoteAddress64;
    private String ni;
    private short parentAddress16;
    private byte deviceType;
    private byte status;
    private short profileId;
    private int manufacturerId;
    
    public static NodeIdentification parse(byte[] data) {
        NodeIdentification ni = new NodeIdentification();
        FrameBuffer frameBuffer = new FrameBuffer(data);
        frameBuffer.setByteOrder(FrameBuffer.BO_BIG_ENDIAN);
        ni.drain(frameBuffer);
        return ni;
    }
    
    public void drain(FrameBuffer frameBuffer) {
        remoteAddress16 = frameBuffer.getShort();
        remoteAddress64 = frameBuffer.getInt64();
        
        frameBuffer.mark();
        int strLen = 1;
        while (frameBuffer.getByte() != 0x00) {
            strLen++;
        }
        frameBuffer.reset();
        ni = new String(frameBuffer.getByteArray(strLen));
        
        parentAddress16 = frameBuffer.getShort();
        deviceType = frameBuffer.getByte();
        status = frameBuffer.getByte();
        profileId = frameBuffer.getShort();
        manufacturerId = frameBuffer.getInt16();
    }

    public void pull(FrameBuffer frameBuffer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int quote() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @return the remoteAddress16
     */
    public short getRemoteAddress16() {
        return remoteAddress16;
    }

    /**
     * @return the remoteAddress64
     */
    public long getRemoteAddress64() {
        return remoteAddress64;
    }

    /**
     * @return the ni
     */
    public String getNi() {
        return ni;
    }

    /**
     * @return the parentAddress16
     */
    public short getParentAddress16() {
        return parentAddress16;
    }

    /**
     * @return the deviceType
     */
    public byte getDeviceType() {
        return deviceType;
    }

    /**
     * @return the status
     */
    public byte getStatus() {
        return status;
    }

    /**
     * @return the profileId
     */
    public short getProfileId() {
        return profileId;
    }

    /**
     * @return the manufacturerId
     */
    public int getManufacturerId() {
        return manufacturerId;
    }
}
