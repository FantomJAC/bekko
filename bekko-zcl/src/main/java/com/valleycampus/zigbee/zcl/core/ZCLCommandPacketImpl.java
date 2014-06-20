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
package com.valleycampus.zigbee.zcl.core;

import com.valleycampus.zigbee.ZigBeeAddress;
import com.valleycampus.zigbee.ZigBeeDataPacket;
import com.valleycampus.zigbee.zcl.ZCLCommandPacket;
import com.valleycampus.zigbee.zcl.command.ZCLCommand;
import com.valleycampus.zigbee.io.ByteUtil;
import com.valleycampus.zigbee.io.Frame;
import com.valleycampus.zigbee.io.FrameBuffer;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class ZCLCommandPacketImpl implements ZCLCommandPacket, Frame {

    private ZigBeeAddress address;
    private int endpoint;
    private short clusterId;
    private boolean clusterSpecific;
    private boolean fromServer;
    private boolean disableDefaultResponse;
    private int tsn;
    private byte commandId;
    private short manufacturerCode;
    private boolean manufacturerSpecific;
    private int offset;
    private int length;
    private byte[] payload;
    
    protected ZCLCommandPacketImpl() {

    }
    
    public static ZCLCommandPacket createResponsePacket(ZCLCommandPacket commandPacket) {
        ZCLCommandPacketImpl response = new ZCLCommandPacketImpl();
        response.setAddress(commandPacket.getAddress());
        response.setEndpoint(commandPacket.getEndpoint());
        response.setClusterId(commandPacket.getClusterId());
        response.setTsn(commandPacket.getTsn());
        response.setDisableDefaultResponse(commandPacket.isDisableDefaultResponse());
        response.setFromServer(!commandPacket.isFromServer());
        return response;
    }
        
    public static ZCLCommandPacket toZCLCommandPacket(ZigBeeDataPacket dataPacket) {
        ZCLCommandPacketImpl zclPacket = new ZCLCommandPacketImpl();
        zclPacket.drain(new FrameBuffer(
                FrameBuffer.BO_LITTLE_ENDIAN,
                dataPacket.getData(), dataPacket.getOffset(), dataPacket.getLength()));
        zclPacket.setAddress(dataPacket.getAddress());
        zclPacket.setEndpoint(dataPacket.getEndpoint());
        zclPacket.setClusterId(dataPacket.getClusterId());
        return zclPacket;
    }
    
    public static void setupSendZigBeeDataPacket(ZigBeeDataPacket dataPacket, ZCLCommandPacket commandPacket) {
        ZCLCommandPacketImpl commandPacketImpl = (ZCLCommandPacketImpl) commandPacket;
        FrameBuffer frameBuffer = new FrameBuffer(FrameBuffer.BO_LITTLE_ENDIAN, new byte[commandPacketImpl.quote()]);
        commandPacketImpl.pull(frameBuffer);
        dataPacket.setData(frameBuffer.getRawArray());
        dataPacket.setAddress(commandPacket.getAddress());
        dataPacket.setEndpoint(commandPacket.getEndpoint());
        dataPacket.setClusterId(commandPacket.getClusterId());
    }
    
    public static FrameBuffer getPayloadAsFrameBuffer(ZCLCommandPacket packet) {
        return new FrameBuffer(FrameBuffer.BO_LITTLE_ENDIAN,
                packet.getPayload(), packet.getPayloadOffset(), packet.getPayloadLength());
    }
    
    public void setZCLCommand(ZCLCommand command) {
        if (command == null) {
            return;
        }
        if (command.quote() != 0) {
            FrameBuffer frameBuffer = new FrameBuffer(FrameBuffer.BO_LITTLE_ENDIAN, new byte[command.quote()]);
            command.pull(frameBuffer);
            setPayload(frameBuffer.getRawArray(), frameBuffer.getOffset(), frameBuffer.getPosition());
        }
        setCommandId(command.getCommandId());
    }

    public void pull(FrameBuffer frameBuffer) {
        int frameControl = 0;
        if (isClusterSpecific()) {
            frameControl |= 0x01;
        }
        if (isManufacturerSpecific()) {
            frameControl |= 0x04;
        }
        if (isFromServer()) {
            frameControl |= 0x08;
        }
        if (isDisableDefaultResponse()) {
            frameControl |= 0x10;
        }
        frameBuffer.putInt8(frameControl);
        if (isManufacturerSpecific()) {
            frameBuffer.putInt16(manufacturerCode);
        }
        frameBuffer.putInt8(tsn);
        frameBuffer.put(commandId);
        if (payload != null) {
            frameBuffer.put(payload, offset, length);
        }
    }

    public int quote() {
        int q = 0;
        q += ByteUtil.INT_8_SIZE;
        if (isManufacturerSpecific()) {
            q += ByteUtil.INT_16_SIZE;
        }
        q += ByteUtil.INT_8_SIZE;
        q += ByteUtil.INT_8_SIZE;
        if (payload != null) {
            q += length;
        }
        return q;
    }

    public void drain(FrameBuffer frameBuffer) {
        byte frameControl = frameBuffer.get();
        clusterSpecific = (frameControl & 0x03) > 0;
        if ((frameControl & 0x04) > 0) {
            manufacturerSpecific = true;
            manufacturerCode = frameBuffer.getShort();
        }
        fromServer = (frameControl & 0x08) > 0;
        disableDefaultResponse = (frameControl & 0x10) > 0;
        tsn = frameBuffer.getInt8();
        commandId = frameBuffer.get();
        
        int len = frameBuffer.getRemaining();
        int off = frameBuffer.getOffset() + frameBuffer.getPosition();
        setPayload(frameBuffer.getRawArray(), off, len);
    }

    /**
     * @return the address
     */
    public ZigBeeAddress getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(ZigBeeAddress address) {
        this.address = address;
    }

    /**
     * @return the endpoint
     */
    public int getEndpoint() {
        return endpoint;
    }

    /**
     * @param endpoint the endpoint to set
     */
    public void setEndpoint(int endpoint) {
        this.endpoint = endpoint;
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
     * @return the clusterSpecific
     */
    public boolean isClusterSpecific() {
        return clusterSpecific;
    }

    /**
     * @param clusterSpecific the clusterSpecific to set
     */
    public void setClusterSpecific(boolean clusterSpecific) {
        this.clusterSpecific = clusterSpecific;
    }

    /**
     * @return the fromServer
     */
    public boolean isFromServer() {
        return fromServer;
    }

    /**
     * @param fromServer the fromServer to set
     */
    public void setFromServer(boolean fromServer) {
        this.fromServer = fromServer;
    }

    /**
     * @return the disableDefaultResponse
     */
    public boolean isDisableDefaultResponse() {
        return disableDefaultResponse;
    }

    /**
     * @param disableDefaultResponse the disableDefaultResponse to set
     */
    public void setDisableDefaultResponse(boolean disableDefaultResponse) {
        this.disableDefaultResponse = disableDefaultResponse;
    }

    /**
     * @return the tsn
     */
    public int getTsn() {
        return tsn;
    }

    /**
     * @param tsn the tsn to set
     */
    public void setTsn(int tsn) {
        this.tsn = tsn;
    }


    /**
     * @return the commandId
     */
    public byte getCommandId() {
        return commandId;
    }

    /**
     * @param commandId the commandId to set
     */
    public void setCommandId(byte commandId) {
        this.commandId = commandId;
    }

    /**
     * @return the manufacturerCode
     */
    public short getManufacturerCode() {
        return manufacturerCode;
    }

    /**
     * @param manufacturerCode the manufacturerCode to set
     */
    public void setManufacturerCode(short manufacturerCode) {
        this.manufacturerCode = manufacturerCode;
    }

    /**
     * @return the manufacturerSpecific
     */
    public boolean isManufacturerSpecific() {
        return manufacturerSpecific;
    }

    /**
     * @param manufacturerSpecific the manufacturerSpecific to set
     */
    public void setManufacturerSpecific(boolean manufacturerSpecific) {
        this.manufacturerSpecific = manufacturerSpecific;
    }

    /**
     * @return the offset
     */
    public int getPayloadOffset() {
        return offset;
    }

    /**
     * @return the length
     */
    public int getPayloadLength() {
        return length;
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
        this.offset = 0;
        this.length = (payload != null) ? payload.length : 0;
    }

    public void setPayload(byte[] payload, int offset, int length) {
        this.payload = payload;
        this.offset = offset;
        this.length = length;
    }
}
