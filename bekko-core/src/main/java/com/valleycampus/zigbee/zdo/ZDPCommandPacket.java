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
package com.valleycampus.zigbee.zdo;

import com.valleycampus.zigbee.ZigBeeAddress;
import com.valleycampus.zigbee.io.ByteUtil;
import com.valleycampus.zigbee.io.Frame;
import com.valleycampus.zigbee.io.FrameBuffer;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class ZDPCommandPacket implements Frame {
    
    private ZigBeeAddress remoteAddress;
    private ZigBeeAddress localAddress;
    private short clusterId;
    private int tsn;
    private int offset;
    private int length;
    private byte[] payload;
    
    public static FrameBuffer toFrameBuffer(ZDPCommandPacket packet) {
        return new FrameBuffer(FrameBuffer.BO_LITTLE_ENDIAN,
                packet.getPayload(), packet.getPayloadOffset(), packet.getPayloadLength());
    }
    
    public void pull(FrameBuffer frameBuffer) {
        frameBuffer.putInt8(tsn);
        if (payload != null) {
            frameBuffer.put(payload, offset, length);
        }
    }

    public int quote() {
        int q = 0;
        q += ByteUtil.INT_8_SIZE;
        if (payload != null) {
            q += length;
        }
        return q;
    }

    public void drain(FrameBuffer frameBuffer) {
        tsn = frameBuffer.getInt8();
        int len = frameBuffer.getRemaining();
        int off = frameBuffer.getOffset() + frameBuffer.getPosition();
        setPayload(frameBuffer.getRawArray(), off, len);
    }
    
    /**
     * @return the remoteAddress
     */
    public ZigBeeAddress getRemoteAddress() {
        return remoteAddress;
    }
    
    public ZigBeeAddress getLocalAddress() {
        return localAddress;
    }
    
    /**
     * @return the clusterId
     */
    public short getClusterId() {
        return clusterId;
    }

    /**
     * @return the tsn
     */
    public int getTsn() {
        return tsn;
    }
    
    /**
     * @return the payload
     */
    public byte[] getPayload() {
        return payload;
    }
    
    /**
     * @param remoteAddress the remoteAddress to set
     */
    public void setRemoteAddress(ZigBeeAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }
    
    public void setLocalAddress(ZigBeeAddress localAddress) {
        this.localAddress = localAddress;
    }
    
    /**
     * @param tsn the tsn to set
     */
    public void setTsn(int tsn) {
        this.tsn = tsn;
    }
    
    /**
     * @return the offset
     */
    public int getPayloadOffset() {
        return offset;
    }

    /**
     * @return the length
     */
    public int getPayloadLength() {
        return length;
    }
    
    /**
     * @param clusterId the clusterId to set
     */
    public void setClusterId(short clusterId) {
        this.clusterId = clusterId;
    }
    
    /**
     * @param payload the payload to set
     */
    public void setPayload(byte[] payload) {
        this.payload = payload;
        this.offset = 0;
        this.length = payload.length;
    }

    public void setPayload(byte[] payload, int offset, int length) {
        this.payload = payload;
        this.offset = offset;
        this.length = length;
    }
    
    public void fillPayload(Frame commandFrame) {
        FrameBuffer frameBuffer = new FrameBuffer(FrameBuffer.BO_LITTLE_ENDIAN, new byte[commandFrame.quote()]);
        commandFrame.pull(frameBuffer);
        setPayload(frameBuffer.getRawArray(), frameBuffer.getOffset(), frameBuffer.getPosition());
    }
}
