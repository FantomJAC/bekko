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

import com.valleycampus.zigbee.io.FrameBuffer;
import com.valleycampus.xbee.api.XBeeRequest;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class XBeeAddressingRequest extends XBeeRequest {
    
    public static final long XBEE_BROADCAST64 = 0x000000000000FFFFL;
    public static final short XBEE_BROADCAST16 = (short) 0xFFFE;

    private long address64;
    private short address16;

    public void pull(FrameBuffer frameBuffer) {
        super.pull(frameBuffer);
        frameBuffer.putInt64(address64);
        frameBuffer.putInt16(address16);
    }
    
    public int quote() {
        return super.quote() + 10;
    }

    /**
     * @return the address64
     */
    public long getAddress64() {
        return address64;
    }

    /**
     * @param address64 the address64 to set
     */
    public void setAddress64(long address64) {
        this.address64 = address64;
    }

    /**
     * @return the address16
     */
    public short getAddress16() {
        return address16;
    }

    /**
     * @param address16 the address16 to set
     */
    public void setAddress16(short address16) {
        this.address16 = address16;
    }
}
