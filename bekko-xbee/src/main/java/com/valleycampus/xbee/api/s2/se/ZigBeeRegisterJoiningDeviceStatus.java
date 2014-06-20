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
package com.valleycampus.xbee.api.s2.se;

import com.valleycampus.zigbee.io.FrameBuffer;
import com.valleycampus.xbee.api.XBeeFrameIdResponse;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class ZigBeeRegisterJoiningDeviceStatus extends XBeeFrameIdResponse {

    public static final int DEVSTS_SUCCESS = 0;
    // 3x19 Only
    public static final int DEVSTS_ADDRESS_INVALID = 0xB3;
    public static final int DEVSTS_KEY_NOTFOUND = 0xFF;
    // 3x1A Only
    public static final int DEVSTS_KEY_TOO_LONG = 0x01;
    public static final int DEVSTS_ADDRESS_NOTFOUND = 0xB1;
    public static final int DEVSTS_KEY_INVALID = 0xB2;
    public static final int DEVSTS_KEYTABLE_FULL = 0xB4;
    private int status;

    public void drain(FrameBuffer frameBuffer) {
        super.drain(frameBuffer);
        status = frameBuffer.getInt8();
    }

    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }
}
