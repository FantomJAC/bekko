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
package com.valleycampus.zigbee;

/**
 * ZigBee APS Data Packet.
 * 
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public interface ZigBeeDataPacket {
    
    public ZigBeeAddress getAddress();
    
    public ZigBeeAddress getLocalAddress();

    public byte[] getData();
    
    public int getLength();
    
    public int getOffset();
    
    public int getEndpoint();
    
    public int getLocalEndpoint();
    
    public short getClusterId();
    
    public byte getStatus();
    
    public byte getSecurityStatus();
    
    public int getLinkQuality();
    
    public int getTime();
    
    public void setAddress(ZigBeeAddress address);
    
    public void setLocalAddress(ZigBeeAddress address);
    
    public void setData(byte[] data);
    
    public void setData(byte[] data, int offset, int length);
    
    public void setEndpoint(int endpoint);
    
    public void setLocalEndpoint(int endpoint);
    
    public void setClusterId(short clusterId);

    public void setStatus(byte status);
    
    public void setSecurityStatus(byte securityStatus);
    
    public void setLinkQuality(int linkQuality);
    
    public void setTime(int time);
}
