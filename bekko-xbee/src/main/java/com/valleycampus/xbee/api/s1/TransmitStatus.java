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
package com.valleycampus.xbee.api.s1;

import com.valleycampus.zigbee.io.FrameBuffer;
import com.valleycampus.xbee.api.XBeeFrameIdResponse;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class TransmitStatus extends XBeeFrameIdResponse {

    public static final byte STATUS_SUCCESS = 0;
    public static final byte STATUS_NO_ACK = 1;
    public static final byte STATUS_CCA_FAILURE = 2;
    public static final byte STATUS_PURGED = 3;
    
    private byte status;

    public void drain(FrameBuffer frameBuffer) {
        super.drain(frameBuffer);
        this.status = frameBuffer.getByte();

    }

    /**
     * @return the status
     */
    public byte getStatus() {
        return status;
    }
}
