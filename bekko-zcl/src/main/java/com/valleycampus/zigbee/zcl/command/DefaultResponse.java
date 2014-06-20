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
package com.valleycampus.zigbee.zcl.command;

import com.valleycampus.zigbee.zcl.ZCLCommandPacket;
import com.valleycampus.zigbee.zcl.core.ZCLCommandPacketImpl;
import com.valleycampus.zigbee.io.FrameBuffer;
import com.valleycampus.zigbee.io.ByteUtil;

/**
 *
 * @author Eisuke Yasato <eyasato@valleycampus.com>
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class DefaultResponse implements ZCLCommand {

    private byte commandId;
    private byte status;
    
    public byte getCommandId() {
        return DEFAULT_RSP;
    }

    public void pull(FrameBuffer frameBuffer) {
        frameBuffer.putInt8(commandId);
        frameBuffer.putInt8(status);
    }

    public int quote() {
        int q = 0;
        q += ByteUtil.INT_8_SIZE;
        q += ByteUtil.INT_8_SIZE;

        return q;
    }

    public void drain(FrameBuffer frameBuffer) {
        commandId = (byte) frameBuffer.getInt8();
        status = (byte) frameBuffer.getInt8();
    }

    /**
     * @return the commandID
     */
    public byte getResponseCommandId() {
        return commandId;
    }

    /**
     * @param commandId the commandID to set
     */
    public void setResponseCommandId(byte commandId) {
        this.commandId = commandId;
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
    
    public static boolean isDefaultResponse(ZCLCommandPacket packet) {
        return !packet.isClusterSpecific() && packet.getCommandId() == ZCLCommand.DEFAULT_RSP;
    }
    
    public static ZCLCommandPacket doDefaultResponse(ZCLCommandPacket packet, byte status) {
        ZCLCommandPacket response = ZCLCommandPacketImpl.createResponsePacket(packet);
        response.setCommandId(ZCLCommand.DEFAULT_RSP);
        response.setClusterSpecific(false);
        response.setManufacturerSpecific(false);
        response.setPayload(new byte[] {packet.getCommandId(), status});
        return response;
    }
}
