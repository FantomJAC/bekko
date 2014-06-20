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

import com.valleycampus.zigbee.io.FrameBuffer;
import com.valleycampus.zigbee.io.ByteUtil;

/**
 *
 * @author Eisuke Yasato <eyasato@valleycampus.com>
 */
public class MatchDescReq extends DescReq {

    private short profileID;
    private short[] inClusterList;
    private short[] outClusterList;

    public void pull(FrameBuffer frameBuffer) {
        super.pull(frameBuffer);
        
        frameBuffer.putInt16(profileID);
        
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
        int q = super.quote();
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
        super.drain(frameBuffer);
        profileID = frameBuffer.getShort();
        int numInClusters = frameBuffer.getInt8();
        inClusterList = new short[numInClusters];
        for(int i = 0; i < numInClusters; i++) {
            inClusterList[i] = frameBuffer.getShort();
        }
        int numOutClusters = frameBuffer.getInt8();
        outClusterList = new short[numOutClusters];
        for(int i = 0; i < numOutClusters; i++) {
            outClusterList[i] = frameBuffer.getShort();
        }
    }

    /**
     * @return the profileID
     */
    public short getProfileID() {
        return profileID;
    }

    /**
     * @param profileID the profileID to set
     */
    public void setProfileID(short profileID) {
        this.profileID = profileID;
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
     * @return the outClustersList
     */
    public short[] getOutClusterList() {
        return outClusterList;
    }

    /**
     * @param outClustersList the outClustersList to set
     */
    public void setOutClusterList(short[] outClusterList) {
        this.outClusterList = outClusterList;
    }
}
