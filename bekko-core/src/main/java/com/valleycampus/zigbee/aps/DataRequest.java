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
public class DataRequest extends DataSupport {

    // Extended TX Options, not specified in the ZigBee spec.
    public static final int EXT_OPTION_SOURCE_EUI64 = 0x0100;
    public static final int EXT_OPTION_DESTINATION_EUI64 = 0x0200;
    
    private ZigBeeAddress destinationAddress;
    private int destinationEndpoint;
    private short profileId;
    private short clusterId;
    private int sourceEndpoint;
    private int txOptions;
    private int radius;

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
     * @return the txOptions
     */
    public int getTxOptions() {
        return txOptions;
    }

    /**
     * @param txOptions the txOptions to set
     */
    public void setTxOptions(int txOptions) {
        this.txOptions = txOptions;
    }

    /**
     * @return the radius
     */
    public int getRadius() {
        return radius;
    }

    /**
     * @param radius the radius to set
     */
    public void setRadius(int radius) {
        this.radius = radius;
    }
}
