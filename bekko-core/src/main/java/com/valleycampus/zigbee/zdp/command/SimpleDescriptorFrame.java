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

import com.valleycampus.zigbee.ZigBeeSimpleDescriptor;
import com.valleycampus.zigbee.io.ByteUtil;
import com.valleycampus.zigbee.io.Frame;
import com.valleycampus.zigbee.io.FrameBuffer;

/**
 *
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public class SimpleDescriptorFrame implements Frame, ZigBeeSimpleDescriptor {
    
    private int endpoint;
    private short profileId;
    private short deviceId;
    private byte deviceVersion;
    private short[] inClusterList;
    private short[] outClusterList;
    
    public SimpleDescriptorFrame() {
        
    }
    
    public SimpleDescriptorFrame(ZigBeeSimpleDescriptor simpleDescriptor) {
        endpoint = simpleDescriptor.getEndpoint();
        profileId = simpleDescriptor.getApplicationProfileIdentifier();
        deviceId = simpleDescriptor.getApplicationDeviceIdentifier();
        deviceVersion = simpleDescriptor.getApplicationDeviceVersion();
        inClusterList = new short[simpleDescriptor.getApplicationInputClusterCount()];
        System.arraycopy(simpleDescriptor.getApplicationInputClusterList(), 0, inClusterList, 0, inClusterList.length);
        outClusterList = new short[simpleDescriptor.getApplicationOutputClusterCount()];
        System.arraycopy(simpleDescriptor.getApplicationOutputClusterList(), 0, outClusterList, 0, outClusterList.length);
    }

    public void pull(FrameBuffer frameBuffer) {
        frameBuffer.putInt8(endpoint);
        frameBuffer.putInt16(profileId);
        frameBuffer.putInt16(deviceId);
        frameBuffer.putInt8(deviceVersion << 4);
        frameBuffer.putInt8(inClusterList.length);
        for (int i = 0; i < inClusterList.length; i++) {
            frameBuffer.putInt16(inClusterList[i]);
        }
        frameBuffer.putInt8(outClusterList.length);
        for (int i = 0; i < outClusterList.length; i++) {
            frameBuffer.putInt16(outClusterList[i]);
        }
    }

    public int quote() {
        int q = 0;
        q += ByteUtil.INT_8_SIZE;
        q += ByteUtil.INT_16_SIZE;
        q += ByteUtil.INT_16_SIZE;
        q += ByteUtil.INT_8_SIZE;
        q += ByteUtil.INT_8_SIZE;
        if (inClusterList != null && inClusterList.length > 0) {
            q += ByteUtil.INT_16_SIZE * inClusterList.length;
        }
        q += ByteUtil.INT_8_SIZE;
        if (outClusterList != null && outClusterList.length > 0) {
            q += ByteUtil.INT_16_SIZE * outClusterList.length;
        }
        return q;
    }

    public void drain(FrameBuffer frameBuffer) {
        endpoint = frameBuffer.getInt8();
        profileId = frameBuffer.getShort();
        deviceId = frameBuffer.getShort();
        deviceVersion = (byte) ((frameBuffer.getInt8() >> 4) & 0x0F);
        int inClusterCount = frameBuffer.getInt8();
        inClusterList = new short[inClusterCount];
        for (int i = 0; i < inClusterCount; i++) {
            inClusterList[i] = frameBuffer.getShort();
        }
        int outClusterCount = frameBuffer.getInt8();
        outClusterList = new short[outClusterCount];
        for (int i = 0; i < outClusterCount; i++) {
            outClusterList[i] = frameBuffer.getShort();
        }
    }

    public int getEndpoint() {
        return endpoint;
    }
    
    public void setEndpoint(int endpoint) {
        this.endpoint = endpoint;
    }

    public short getApplicationProfileIdentifier() {
        return profileId;
    }
        
    public void setApplicationProfileIdentifier(short profileId) {
        this.profileId = profileId;
    }
    
    public short getApplicationDeviceIdentifier() {
        return deviceId;
    }
    
    public void setApplicationDeviceIdentifier(short deviceId) {
        this.deviceId = deviceId;
    }

    public byte getApplicationDeviceVersion() {
        return deviceVersion;
    }
       
    public void setApplicationDeviceVersion(byte deviceVersion) {
        this.deviceVersion = deviceVersion;
    }

    public int getApplicationInputClusterCount() {
        return inClusterList.length;
    }
    
    public void setApplicationInputClusterList(short[] inClusterList) {
        this.inClusterList = inClusterList;
    }
    
    public short[] getApplicationInputClusterList() {
        return inClusterList;
    }
   
    public int getApplicationOutputClusterCount() {
        return outClusterList.length;
    }

    public short[] getApplicationOutputClusterList() {
        return outClusterList;
    }
    
    public void setApplicationOutputClusterList(short[] outClusterList) {
        this.outClusterList = outClusterList;
    }
}
