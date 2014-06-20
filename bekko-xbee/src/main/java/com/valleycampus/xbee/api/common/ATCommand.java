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
package com.valleycampus.xbee.api.common;

import com.valleycampus.xbee.api.XBeeRequest;
import com.valleycampus.zigbee.io.FrameBuffer;


/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class ATCommand extends XBeeRequest {
    
    public static final int COMMAND_SIZE = 2;

    private String command;
    private byte[] value;

    public ATCommand() {
        setFrameType(API_AT_COMMAND);
    }
    
    public ATCommand(String command) {
        this();
        setCommand(command);
    }

    public void pull(FrameBuffer frameBuffer) {
        super.pull(frameBuffer);
        frameBuffer.putInt8(command.charAt(0));
        frameBuffer.putInt8(command.charAt(1));
        if (value != null) {
            frameBuffer.put(value);
        }
    }

    public int quote() {
        int q = super.quote() + COMMAND_SIZE;
        if (value != null) {
            q += value.length;
        }
        return q;
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
