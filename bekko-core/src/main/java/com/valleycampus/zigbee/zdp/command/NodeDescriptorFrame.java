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

import com.valleycampus.zigbee.ZigBeeNodeDescriptor;
import com.valleycampus.zigbee.io.ByteUtil;
import com.valleycampus.zigbee.io.Frame;
import com.valleycampus.zigbee.io.FrameBuffer;
import com.valleycampus.zigbee.util.Commons;

/**
 *
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public class NodeDescriptorFrame implements Frame, ZigBeeNodeDescriptor{

    private short nodeInfo;
    private byte capability;
    private short manufacturerCode;
    private int maximumBufferSize;
    private int maximumIncomingTransferSize;
    private short serverMask;
    private int maximumOutgoingTransferSize;
    private byte descriptorCapability;
    
    public void pull(FrameBuffer frameBuffer) {
        frameBuffer.putInt16(nodeInfo);
        frameBuffer.putInt8(capability);
        frameBuffer.putInt16(manufacturerCode);
        frameBuffer.putInt8(maximumBufferSize);
        frameBuffer.putInt8(maximumIncomingTransferSize);
        frameBuffer.putInt16(serverMask);
        frameBuffer.putInt8(maximumOutgoingTransferSize);
        frameBuffer.putInt8(descriptorCapability);
    }

    public int quote() {
        return 11 * ByteUtil.BYTE_SIZE;
    }

    public void drain(FrameBuffer frameBuffer) {
        nodeInfo = frameBuffer.getShort();
        capability = frameBuffer.getByte();
        manufacturerCode = frameBuffer.getShort();
        maximumBufferSize = frameBuffer.getInt8();
        maximumIncomingTransferSize = frameBuffer.getInt8();
        serverMask = frameBuffer.getShort();
        maximumOutgoingTransferSize = frameBuffer.getInt8();
        descriptorCapability = frameBuffer.getByte();
    }

    public byte getLogicalType() {
        return (byte) Commons.getField(nodeInfo, 0, 3);
    }
    
    public void setLogicalType(byte logicalType) {
        nodeInfo = (short) Commons.setField(logicalType, nodeInfo, 0, 3);
    }
    
    public boolean isComplexDescriptorAvailable() {
        return Commons.getField(nodeInfo, 3, 1) > 0;
    }
    
    public void setComplexDescriptorAvailable(boolean complexDescriptorAvailable) {
        nodeInfo = (short) Commons.setField(complexDescriptorAvailable ? 1 : 0, nodeInfo, 3, 1);
    }

    public boolean isUserDescriptorAvailable() {
        return Commons.getField(nodeInfo, 4, 1) > 0;
    }
    
    public void setUserDescriptorAvailable(boolean userDescriptorAvailable) {
        nodeInfo = (short) Commons.setField(userDescriptorAvailable ? 1 : 0, nodeInfo, 4, 1);
    }

    public byte getAPSFlags() {
        return (byte) Commons.getField(nodeInfo, 8, 3);
    }
    
    public void setAPSFlags(byte apsFlags) {
        nodeInfo = (short) Commons.setField(apsFlags, nodeInfo, 8, 3);
    }

    public byte getFrequencyBand() {
        return (byte) Commons.getField(nodeInfo, 12, 5);
    }
    
    public void setFrequencyBand(byte frequencyBand) {
        nodeInfo = (short) Commons.setField(frequencyBand, nodeInfo, 12, 5);
    }
 
    public byte getMACCapabilityFlags() {
        return capability;
    }
    
    public void setMACCapabilityFlags(byte capability) {
        this.capability = capability;
    }

    public short getManufacturerCode() {
        return manufacturerCode;
    }
    
    public void setManufacturerCode(short manufacturerCode) {
        this.manufacturerCode = manufacturerCode;
    }

    public int getMaximumBufferSize() {
        return maximumBufferSize;
    }
    
    public void setMaximumBufferSize(int maximumBufferSize) {
        this.maximumBufferSize = maximumBufferSize;
    }

    public int getMaximumIncomingTransferSize() {
        return maximumIncomingTransferSize;
    }
    
    public void setMaximumIncomingTransferSize(int maximumIncomingTransferSize) {
        this.maximumIncomingTransferSize = maximumIncomingTransferSize;
    }

    public short getServerMask() {
        return serverMask;
    }
    
    public void setServerMask(short serverMask) {
        this.serverMask = serverMask;
    }

    public int getMaximumOutgoingTransferSize() {
        return maximumOutgoingTransferSize;
    }
    
    public void setMaximumOutgoingTransferSize(int maximumOutgoingTransferSize) {
        this.maximumOutgoingTransferSize = maximumOutgoingTransferSize;
    }

    public byte getDescriptorCapability() {
        return descriptorCapability;
    }
    
    public void setDescriptorCapability(byte descriptorCapability) {
        this.descriptorCapability = descriptorCapability;
    }
}
