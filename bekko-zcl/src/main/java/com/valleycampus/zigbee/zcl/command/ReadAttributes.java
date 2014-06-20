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

import com.valleycampus.zigbee.io.FrameBuffer;
import com.valleycampus.zigbee.io.ByteUtil;

/**
 *
 * @author Eisuke Yasato <eyasato@valleycampus.com>
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class ReadAttributes implements ZCLCommand {

    private short[] attributesId;
    
    public byte getCommandId() {
        return READ_ATTRIBUTES;
    }


    public void pull(FrameBuffer frameBuffer) {
        for (int i = 0; i < attributesId.length; i++) {
            frameBuffer.putInt16(attributesId[i]);
        }
    }

    public int quote() {
        int q = 0;
        q += attributesId.length * ByteUtil.INT_16_SIZE;
        return q;
    }

    public void drain(FrameBuffer frameBuffer) {
        int length = frameBuffer.getRemaining() / 2;
        attributesId = new short[length];
        for (int i = 0; i < length; i++) {
            attributesId[i] = frameBuffer.getShort();
        }
    }

    /**
     * @return the attributesID
     */
    public short[] getAttributesId() {
        return attributesId;
    }

    /**
     * @param attributesId the attributesID to set
     */
    public void setAttributesId(short[] attributesId) {
        this.attributesId = attributesId;
    }
}
