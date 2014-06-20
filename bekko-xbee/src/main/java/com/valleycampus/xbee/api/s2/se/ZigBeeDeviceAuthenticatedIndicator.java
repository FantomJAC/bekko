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
import com.valleycampus.xbee.api.s2.XBeeAddressingIndicator;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class ZigBeeDeviceAuthenticatedIndicator extends XBeeAddressingIndicator {

    public static final int STATUS_SUCCESS = 0;
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
