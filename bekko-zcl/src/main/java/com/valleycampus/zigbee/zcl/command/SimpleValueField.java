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

import com.valleycampus.zigbee.io.Frame;
import com.valleycampus.zigbee.io.FrameBuffer;

/**
 *
 * @author Eisuke Yasato <eyasato@valleycampus.com>
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class SimpleValueField implements Frame {

    private byte[] value;
    private int octets;
    
    public SimpleValueField(int octets) {
        this.octets = octets;
    }

    public void pull(FrameBuffer frameBuffer) {
        frameBuffer.put(value, 0, octets);
    }

    public int quote() {
        return octets;
    }

    public void drain(FrameBuffer frameBuffer) {
        value = new byte[octets];
        frameBuffer.get(value);
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
