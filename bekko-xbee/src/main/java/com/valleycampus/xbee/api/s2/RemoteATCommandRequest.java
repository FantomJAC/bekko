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

import com.valleycampus.xbee.api.common.ATCommand;
import com.valleycampus.zigbee.io.FrameBuffer;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class RemoteATCommandRequest extends XBeeAddressingRequest {

    private byte remoteCommandOptions;
    private String command;
    private byte[] value;

    public RemoteATCommandRequest() {
        setFrameType(API_REMOTE_COMMAND_REQ);
    }
    
    public void pull(FrameBuffer frameBuffer) {
        super.pull(frameBuffer);
        frameBuffer.put(remoteCommandOptions);
        frameBuffer.putInt8(command.charAt(0));
        frameBuffer.putInt8(command.charAt(1));
        if (value != null) {
            frameBuffer.put(value);
        }
    }

    public int quote() {
        int q = super.quote() + 1 + ATCommand.COMMAND_SIZE;
        if (value != null) {
            q += value.length;
        }
        return q;
    }

    /**
     * @return the remoteCommandOptions
     */
    public byte getRemoteCommandOptions() {
        return remoteCommandOptions;
    }

    /**
     * @param remoteCommandOptions the remoteCommandOptions to set
     */
    public void setRemoteCommandOptions(byte remoteCommandOptions) {
        this.remoteCommandOptions = remoteCommandOptions;
    }
    
    /**
     * @return the command
     */
    public String getCommand() {
        return command;
    }

    /**
     * @param command the command to set
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * @return the value
     */
    public byte[] getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(byte[] value) {
        this.value = value;
    }
}
