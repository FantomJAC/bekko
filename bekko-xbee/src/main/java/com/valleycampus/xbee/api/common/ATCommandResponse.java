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

import com.valleycampus.xbee.api.XBeeFrameIdResponse;
import com.valleycampus.zigbee.io.FrameBuffer;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class ATCommandResponse extends XBeeFrameIdResponse {

    public static final int STATUS_OK = 0;
    public static final int STATUS_ERROR = 1;
    public static final int STATUS_INVALID_COMMAND = 2;
    public static final int STATUS_INVALID_PARAMETER = 3;
    protected String command;
    protected int status;
    protected byte[] data;
    
    public void drain(FrameBuffer frameBuffer) {
        super.drain(frameBuffer);
        command = new String(frameBuffer.getByteArray(ATCommand.COMMAND_SIZE));
        status = frameBuffer.getInt8();
        if (frameBuffer.getRemaining() > 0) {
            data = frameBuffer.getByteArray(frameBuffer.getRemaining());
        }
    }

    /**
     * @return the command
     */
    public String getCommand() {
        return command;
    }

    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @return the data
     */
    public byte[] getData() {
        return data;
    }
}
