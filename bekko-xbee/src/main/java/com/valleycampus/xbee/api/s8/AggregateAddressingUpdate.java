/*
 * Copyright (C) 2013 Valley Campus Japan, Inc.
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
package com.valleycampus.xbee.api.s8;

import com.valleycampus.xbee.api.XBeeResponse;
import com.valleycampus.zigbee.io.FrameBuffer;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class AggregateAddressingUpdate extends XBeeResponse {
    
    private byte formatId;
    private long newAddress;
    private long oldAddress;
    
    public void drain(FrameBuffer frameBuffer) {
        super.drain(frameBuffer);
        formatId = frameBuffer.getByte();
        newAddress = frameBuffer.getInt64();
        oldAddress = frameBuffer.getInt64();
    }

    /**
     * @return the formatId
     */
    public byte getFormatId() {
        return formatId;
    }

    /**
     * @return the newAddress
     */
    public long getNewAddress() {
        return newAddress;
    }

    /**
     * @return the oldAddress
     */
    public long getOldAddress() {
        return oldAddress;
    }
}
