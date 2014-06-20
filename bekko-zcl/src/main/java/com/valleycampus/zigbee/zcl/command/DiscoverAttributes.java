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

/**
 *
 * @author Eisuke Yasato <eyasato@valleycampus.com>
 */
public class DiscoverAttributes implements ZCLCommand {

    private short startAttributeId;
    private byte maxAttributesId;
    
    public byte getCommandId() {
        return DISCOVER_ATTRIBUTES;
    }

    public void pull(FrameBuffer fb) {
        fb.putInt16(startAttributeId);
        fb.putInt8(maxAttributesId);
    }

    public int quote() {
        return 3;
    }

    public void drain(FrameBuffer fb) {
        startAttributeId = fb.getShort();
        maxAttributesId = fb.getByte();
    }

    /**
     * @return the startAttributeId
     */
    public short getStartAttributeId() {
        return startAttributeId;
    }

    /**
     * @param startAttributeId the startAttributeId to set
     */
    public void setStartAttributeId(short startAttributeId) {
        this.startAttributeId = startAttributeId;
    }

    /**
     * @return the maxAttributeId
     */
    public byte getMaxAttributesId() {
        return maxAttributesId;
    }

    /**
     * @param maxAttributesId the maxAttributeId to set
     */
    public void setMaxAttributeId(byte maxAttributesId) {
        this.maxAttributesId = maxAttributesId;
    }

}
