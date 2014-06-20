/*
 * Copyright (C) 2014 Valley Campus Japan, Inc.
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
package com.valleycampus.zigbee.zcl;

import com.valleycampus.zigbee.ZigBeeAddress;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public interface ZCLCommandPacket {
    
    public ZigBeeAddress getAddress();
    
    public int getEndpoint();
    
    public short getClusterId();
    
    public byte getCommandId();
    
    public short getManufacturerCode();
    
    public boolean isManufacturerSpecific();
    
    public boolean isClusterSpecific();
    
    public boolean isFromServer();
    
    public boolean isDisableDefaultResponse();
    
    public int getTsn();
    
    public byte[] getPayload();
    
    public int getPayloadLength();
    
    public int getPayloadOffset();
    
    public void setAddress(ZigBeeAddress address);
    
    public void setEndpoint(int endpoint);
    
    public void setClusterId(short clusterId);
    
    public void setCommandId(byte commandId);
    
    public void setManufacturerCode(short manufacturerCode);
    
    public void setManufacturerSpecific(boolean manufacturerSpecific);
    
    public void setClusterSpecific(boolean clusterSpecific);

    public void setFromServer(boolean fromServer);

    public void setDisableDefaultResponse(boolean disableDefaultResponse);

    public void setTsn(int tsn);
    
    public void setPayload(byte[] data);
    
    public void setPayload(byte[] data, int offset, int length);
}
