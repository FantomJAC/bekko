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
package com.valleycampus.zigbee.aps;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public abstract class DataSupport {
    
    private int length;
    private int offset;
    private byte[] payload;

    /**
     * @return the payload
     */
    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
        this.offset = 0;
        this.length = payload.length;
    }

    public void setPayload(byte[] payload, int offset, int length) {
        this.payload = payload;
        this.offset = offset;
        this.length = length;
    }

    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * @return the offset
     */
    public int getOffset() {
        return offset;
    }
}
