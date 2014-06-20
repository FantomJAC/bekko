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

import com.valleycampus.zigbee.IEEEAddress;
import com.valleycampus.zigbee.NetworkAddress;
import com.valleycampus.zigbee.io.ByteUtil;
import com.valleycampus.zigbee.io.FrameBuffer;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class EndDeviceBindReq implements ZDPCommand {
    
    private NetworkAddress bindingTarget;
    private IEEEAddress srcIEEEAddress;
    private int srcEndpoint;
    private short profileId;
    private short[] inClusterList;
    private short[] outClusterList;

    public void pull(FrameBuffer frameBuffer) {
        frameBuffer
                .putInt16(bindingTarget.toShort())
                .putInt64(srcIEEEAddress.toLong())
                .putInt8(srcEndpoint)
                .putInt16(profileId);
        
        int numInClusters = 0;
        if (inClusterList != null) {
            numInClusters = inClusterList.length;
        }
        frameBuffer.putInt8(numInClusters);
        for (int i = 0; i < numInClusters; i++) {
            frameBuffer.putInt16(inClusterList[i]);
        }
        
        int numOutClusters = 0;
        if (outClusterList != null) {
            numOutClusters = outClusterList.length;
        }
        frameBuffer.putInt8(numOutClusters);
        for (int i = 0; i < numOutClusters; i++) {
            frameBuffer.putInt16(outClusterList[i]);
        }
    }

    public int quote() {
        int q = 0;
        q += ByteUtil.INT_16_SIZE;
        q += ByteUtil.INT_64_SIZE;
        q += ByteUtil.INT_8_SIZE;
        q += ByteUtil.INT_16_SIZE;
        
        int numInClusters = 0;
        if (inClusterList != null) {
            numInClusters = inClusterList.length;
        }
        q += ByteUtil.INT_8_SIZE;
        q += numInClusters * ByteUtil.INT_16_SIZE;
        
        int numOutClusters = 0;
        if (outClusterList != null) {
            numOutClusters = outClusterList.length;
        }
        q += ByteUtil.INT_8_SIZE;
        q += numOutClusters * ByteUtil.INT_16_SIZE;
        
        return q;
    }

    public void drain(FrameBuffer frameBuffer) {
        bindingTarget = NetworkAddress.getByAddress(frameBuffer.getShort());
        srcIEEEAddress = IEEEAddress.getByAddress(frameBuffer.getInt64());
        srcEndpoint = frameBuffer.getInt8();
        profileId = frameBuffer.getShort();
        
        int numInClusters = frameBuffer.getInt8();
        inClusterList = new short[numInClusters];
        for (int i = 0; i < numInClusters; i++) {
            inClusterList[i] = frameBuffer.getShort();
        }
        
        int numOutClusters = frameBuffer.getInt8();
        outClusterList = new short[numOutClusters];
        for (int i = 0; i < numOutClusters; i++) {
            outClusterList[i] = frameBuffer.getShort();
        }
    }

    /**
     * @return the bindingTarget
     */
    public NetworkAddress getBindingTarget() {
        return bindingTarget;
    }

    /**
     * @param bindingTarget the bindingTarget to set
     */
    public void setBindingTarget(NetworkAddress bindingTarget) {
        this.bindingTarget = bindingTarget;
    }

    /**
     * @return the srcIEEEAddress
     */
    public IEEEAddress getSrcIEEEAddress() {
        return srcIEEEAddress;
    }

    /**
     * @param srcIEEEAddress the srcIEEEAddress to set
     */
    public void setSrcIEEEAddress(IEEEAddress srcIEEEAddress) {
        this.srcIEEEAddress = srcIEEEAddress;
    }

    /**
     * @return the srcEndpoint
     */
    public int getSrcEndpoint() {
        return srcEndpoint;
    }

    /**
     * @param srcEndpoint the srcEndpoint to set
     */
    public void setSrcEndpoint(int srcEndpoint) {
        this.srcEndpoint = srcEndpoint;
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
     * @return the inClusterList
     */
    public short[] getInClusterList() {
        return inClusterList;
    }

    /**
     * @param inClusterList the inClusterList to set
     */
    public void setInClusterList(short[] inClusterList) {
        this.inClusterList = inClusterList;
    }

    /**
     * @return the outClusterList
     */
    public short[] getOutClusterList() {
        return outClusterList;
    }

    /**
     * @param outClusterList the outClusterList to set
     */
    public void setOutClusterList(short[] outClusterList) {
        this.outClusterList = outClusterList;
    }
}
