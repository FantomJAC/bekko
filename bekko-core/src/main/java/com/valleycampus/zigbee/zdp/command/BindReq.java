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

import com.valleycampus.zigbee.GroupAddress;
import com.valleycampus.zigbee.IEEEAddress;
import com.valleycampus.zigbee.ZigBeeAddress;
import com.valleycampus.zigbee.aps.DataService;
import com.valleycampus.zigbee.io.FrameBuffer;
import com.valleycampus.zigbee.io.ByteUtil;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class BindReq implements ZDPCommand {

    private IEEEAddress sourceIEEEAddress;
    private int sourceEndpoint;
    private short clusterId;
    private ZigBeeAddress destinationAddress;
    private int destinationEndpoint;
    
    public void pull(FrameBuffer frameBuffer) {
        frameBuffer.putInt64(sourceIEEEAddress.toLong());
        frameBuffer.putInt8(sourceEndpoint);
        frameBuffer.putInt16(clusterId);
        if (destinationAddress instanceof IEEEAddress) {
            frameBuffer.put(DataService.ADDRESS_MODE_EUI64);
            frameBuffer.putInt64(((IEEEAddress) destinationAddress).toLong());
            frameBuffer.putInt8(destinationEndpoint);
        } else if (destinationAddress instanceof GroupAddress) {
            frameBuffer.put(DataService.ADDRESS_MODE_GROUP);
            frameBuffer.putInt16(((GroupAddress) destinationAddress).toShort());
        } else {
            throw new IllegalStateException("Destination address is not either EUI64 or Multicast.");
        }
    }

    public int quote() {
        int q = 0;
        q += IEEEAddress.ADDRESS_SIZE;
        q += ByteUtil.INT_8_SIZE;
        q += ByteUtil.INT_16_SIZE;
        q += ByteUtil.INT_8_SIZE;
        q += destinationAddress.array().length;
        if (destinationAddress instanceof IEEEAddress) {
            q += ByteUtil.INT_8_SIZE;
        }
        return q;
    }

    public void drain(FrameBuffer frameBuffer) {
        sourceIEEEAddress = IEEEAddress.getByAddress(frameBuffer.getInt64());
        sourceEndpoint = frameBuffer.getByte();
        clusterId = frameBuffer.getShort();
        byte addressMode = frameBuffer.getByte();
        if (addressMode == DataService.ADDRESS_MODE_EUI64) {
            destinationAddress = IEEEAddress.getByAddress(frameBuffer.getInt64());
            destinationEndpoint = frameBuffer.getByte();
        } else if (addressMode == DataService.ADDRESS_MODE_GROUP) {
            destinationAddress = GroupAddress.getByAddress(frameBuffer.getShort());
        } else {
            throw new IllegalStateException("Destination address is not either EUI64 or Multicast.");
        }
    }

    /**
     * @return the sourceIEEEAddress
     */
    public IEEEAddress getSourceIEEEAddress() {
        return sourceIEEEAddress;
    }

    /**
     * @param sourceIEEEAddress the sourceIEEEAddress to set
     */
    public void setSourceIEEEAddress(IEEEAddress sourceIEEEAddress) {
        this.sourceIEEEAddress = sourceIEEEAddress;
    }

    /**
     * @return the sourceEndpoint
     */
    public int getSourceEndpoint() {
        return sourceEndpoint;
    }

    /**
     * @param sourceEndpoint the sourceEndpoint to set
     */
    public void setSourceEndpoint(int sourceEndpoint) {
        this.sourceEndpoint = sourceEndpoint;
    }

    /**
     * @return the clusterId
     */
    public short getClusterId() {
        return clusterId;
    }

    /**
     * @param clusterId the clusterId to set
     */
    public void setClusterId(short clusterId) {
        this.clusterId = clusterId;
    }

    /**
     * @return the destinationAddress
     */
    public ZigBeeAddress getDestinationAddress() {
        return destinationAddress;
    }

    /**
     * @param destinationAddress the destinationAddress to set
     */
    public void setDestinationAddress(ZigBeeAddress destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    /**
     * @return the destinationEndpoint
     */
    public int getDestinationEndpoint() {
        return destinationEndpoint;
    }

    /**
     * @param destinationEndpoint the destinationEndpoint to set
     */
    public void setDestinationEndpoint(int destinationEndpoint) {
        this.destinationEndpoint = destinationEndpoint;
    }
}
