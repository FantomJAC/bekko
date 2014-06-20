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
package com.valleycampus.zigbee.aps;

import com.valleycampus.zigbee.ZigBeeAddress;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class DataIndication extends DataSupport {
    
    private ZigBeeAddress destinationAddress;
    private int destinationEndpoint;
    private ZigBeeAddress sourceAddress;
    private int sourceEndpoint;
    private short profileId;
    private short clusterId;
    private byte status;
    private byte securityStatus;
    private int linkQuality;

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

    /**
     * @return the sourceAddress
     */
    public ZigBeeAddress getSourceAddress() {
        return sourceAddress;
    }

    /**
     * @param sourceAddress the sourceAddress to set
     */
    public void setSourceAddress(ZigBeeAddress sourceAddress) {
        this.sourceAddress = sourceAddress;
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
     * @return the profileId
     */
    public short getProfileId() {
        return profileId;
    }

    /**
     * @param profileId the profileId to set
     */
    public void setProfileId(short profileId) {
        this.profileId = profileId;
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
     * @return the status
     */
    public byte getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(byte status) {
        this.status = status;
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
}
