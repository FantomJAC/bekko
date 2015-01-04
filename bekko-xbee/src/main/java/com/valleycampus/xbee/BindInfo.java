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
package com.valleycampus.xbee;

import com.valleycampus.zigbee.ZigBeeAddress;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class BindInfo {
    
    private int sourceEndpoint;
    private int clusterID;
    private ZigBeeAddress destinationAddress;
    private int destinationEndpoint;
    
    public BindInfo() { }
    
    public BindInfo(int sourceEndpoint, short clusterID, ZigBeeAddress destinationAddress, int destinationEndpoint) {
        this.sourceEndpoint = sourceEndpoint;
        this.clusterID = clusterID;
        this.destinationAddress = destinationAddress;
        this.destinationEndpoint = destinationEndpoint;
    }
    
    public boolean isValid() {
        return (0x01 <= sourceEndpoint && sourceEndpoint <= 0xF0) &&
               (0x0000 <= clusterID && clusterID <= 0xFFFF) &&
               (destinationAddress != null) &&
               ((0x01 <= destinationEndpoint && destinationEndpoint <= 0xF0)
                || destinationEndpoint == 0xFF);
    }

    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + this.sourceEndpoint;
        hash = 17 * hash + this.clusterID;
        hash = 17 * hash + (this.destinationAddress != null ? this.destinationAddress.hashCode() : 0);
        hash = 17 * hash + this.destinationEndpoint;
        return hash;
    }
    
    public boolean equals(Object o) {
        if (o instanceof BindInfo) {
            BindInfo info = (BindInfo) o;
            return sourceEndpoint == info.sourceEndpoint &&
                   clusterID == info.clusterID &&
                   destinationAddress.equals(info.destinationAddress) &&
                   destinationEndpoint == info.destinationEndpoint;
        }
        return false;
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
     * @return the clusterID
     */
    public int getClusterID() {
        return clusterID;
    }

    /**
     * @param clusterID the clusterID to set
     */
    public void setClusterID(int clusterID) {
        this.clusterID = clusterID;
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
