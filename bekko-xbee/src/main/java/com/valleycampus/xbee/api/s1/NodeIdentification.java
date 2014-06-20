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

import com.valleycampus.zigbee.io.Frame;
import com.valleycampus.zigbee.io.FrameBuffer;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class NodeIdentification implements Frame {

    private short remoteAddress16;
    private long remoteAddress64;
    private byte receivedSignalStrength;
    private String ni;

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
        receivedSignalStrength = frameBuffer.getByte();
        
        frameBuffer.mark();
        int strLen = 1;
        while (frameBuffer.getByte() != 0x00) {
            strLen++;
        }
        frameBuffer.reset();
        ni = new String(frameBuffer.getByteArray(strLen));
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
     * @param remoteAddress16 the remoteAddress16 to set
     */
    public void setRemoteAddress16(short remoteAddress16) {
        this.remoteAddress16 = remoteAddress16;
    }

    /**
     * @return the remoteAddress64
     */
    public long getRemoteAddress64() {
        return remoteAddress64;
    }

    /**
     * @param remoteAddress64 the remoteAddress64 to set
     */
    public void setRemoteAddress64(long remoteAddress64) {
        this.remoteAddress64 = remoteAddress64;
    }

    /**
     * @return the receivedSignalStrength
     */
    public byte getReceivedSignalStrength() {
        return receivedSignalStrength;
    }

    /**
     * @param receivedSignalStrength the receivedSignalStrength to set
     */
    public void setReceivedSignalStrength(byte receivedSignalStrength) {
        this.receivedSignalStrength = receivedSignalStrength;
    }

    /**
     * @return the ni
     */
    public String getNI() {
        return ni;
    }

    /**
     * @param ni the ni to set
     */
    public void setNI(String ni) {
        this.ni = ni;
    }

}
