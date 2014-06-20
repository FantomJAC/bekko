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
package com.valleycampus.zigbee.apl;

import com.valleycampus.zigbee.ZigBeeAddress;
import com.valleycampus.zigbee.ZigBeeConst;
import com.valleycampus.zigbee.ZigBeeDataPacket;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class ZigBeeDataPacketImpl implements ZigBeeDataPacket {
    
    private ZigBeeAddress address;
    private ZigBeeAddress localAddress;
    private byte[] data;
    private int length;
    private int offset;
    private int endpoint;
    private int localEndpoint;
    private short clusterId;
    private byte status;
    private int linkQuality;
    private byte securityStatus;
    private int time;
    
    public ZigBeeDataPacketImpl() {
        // ZigBeeDataConnection will choose endpoint automatically.
        localEndpoint = ZigBeeConst.BRODCAST_ENDPOINT;
    }

    public ZigBeeAddress getAddress() {
        return address;
    }
    
    public ZigBeeAddress getLocalAddress() {
        return localAddress;
    }

    public byte[] getData() {
        return data;
    }

    public int getLength() {
        return length;
    }

    public int getOffset() {
        return offset;
    }

    public int getEndpoint() {
        return endpoint;
    }
    
    public int getLocalEndpoint() {
        return localEndpoint;
    }

    public short getClusterId() {
        return clusterId;
    }
    
    public byte getStatus() {
        return status;
    }

    public void setAddress(ZigBeeAddress address) {
        this.address = address;
    }
    
    public void setLocalAddress(ZigBeeAddress localAddress) {
        this.localAddress = localAddress;
    }

    public void setData(byte[] data) {
        this.data = data;
        this.offset = 0;
        this.length = data.length;
    }

    public void setData(byte[] data, int offset, int length) {
        this.data = data;
        this.offset = offset;
        this.length = length;
    }

    public void setEndpoint(int endpoint) {
        this.endpoint = endpoint;
    }
    
    public void setLocalEndpoint(int localEndpoint) {
        this.localEndpoint = localEndpoint;
    }

    public void setClusterId(short clusterId) {
        this.clusterId = clusterId;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    /**
     * @return the linkQuality
     */
    public int getLinkQuality() {
        return linkQuality;
    }

    /**
     * @param linkQuality the linkQuality to set
     */
    public void setLinkQuality(int linkQuality) {
        this.linkQuality = linkQuality;
    }

    /**
     * @return the securityStatus
     */
    public byte getSecurityStatus() {
        return securityStatus;
    }

    /**
     * @param securityStatus the securityStatus to set
     */
    public void setSecurityStatus(byte securityStatus) {
        this.securityStatus = securityStatus;
    }

    /**
     * @return the time
     */
    public int getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(int time) {
        this.time = time;
    }
}
