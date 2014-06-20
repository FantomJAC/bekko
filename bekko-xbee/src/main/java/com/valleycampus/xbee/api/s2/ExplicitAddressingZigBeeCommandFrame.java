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
package com.valleycampus.xbee.api.s2;

import com.valleycampus.zigbee.io.FrameBuffer;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class ExplicitAddressingZigBeeCommandFrame extends XBeeAddressingRequest {

    private int sourceEndpoint;
    private int destinationEndpoint;
    private short clusterId;
    private short profileId;
    private byte broadcastRadius;
    private byte transmitOptions;
    private byte[] payload;
    
    public ExplicitAddressingZigBeeCommandFrame() {
        setFrameType(API_EXPLICIT_ADDR_ZB_CMD_FRM);
    }
    
    public void pull(FrameBuffer frameBuffer) {
        super.pull(frameBuffer);
        frameBuffer.putInt8(sourceEndpoint);
        frameBuffer.putInt8(destinationEndpoint);
        frameBuffer.putInt16(clusterId);
        frameBuffer.putInt16(profileId);
        frameBuffer.putInt8(broadcastRadius);
        frameBuffer.putInt8(transmitOptions);
        if (payload != null) {
            frameBuffer.put(payload);
        }
    }
    
    public int quote() {
        int q = super.quote() + 8;
        if (payload != null) {
            q += payload.length;
        }
        return q;
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
     * @return the clusterId
     */
    public short getClusterId() {
        return clusterId;
    }

    /**
     * @param clusterId the clusterID to set
     */
    public void setClusterId(short clusterId) {
        this.clusterId = clusterId;
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
     * @return the broadcastRadius
     */
    public byte getBroadcastRadius() {
        return broadcastRadius;
    }

    /**
     * @param broadcastRadius the broadcastRadius to set
     */
    public void setBroadcastRadius(byte broadcastRadius) {
        this.broadcastRadius = broadcastRadius;
    }

    /**
     * @return the transmitOptions
     */
    public byte getTransmitOptions() {
        return transmitOptions;
    }

    /**
     * @param transmitOptions the transmitOptions to set
     */
    public void setTransmitOptions(byte transmitOptions) {
        this.transmitOptions = transmitOptions;
    }

    /**
     * @return the payload
     */
    public byte[] getPayload() {
        return payload;
    }

    /**
     * @param payload the payload to set
     */
    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

}
