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

import java.util.ArrayList;
import com.valleycampus.zigbee.io.Frame;
import com.valleycampus.zigbee.io.FrameBuffer;

/**
 *
 * @author Eisuke Yasato <eyasato@valleycampus.com>
 */
public class DiscoverAttributesResponse implements ZCLCommand {
    
    private boolean discoveryComp;
    private AttributeInfo[] attributesInfo;
    
    public byte getCommandId() {
        return DISCOVER_ATTRIBUTES_RSP;
    }

    public void pull(FrameBuffer frameBuffer) {
        frameBuffer.putBoolean(discoveryComp);
        for(int i = 0; i < attributesInfo.length; i++) {
            attributesInfo[i].pull(frameBuffer);
        }
    }

    public int quote() {
        int q = 0;
        for(int i = 0; i < attributesInfo.length; i++) {
            q += attributesInfo[i].quote();
        }
        
        return q + 1;
    }

    public void drain(FrameBuffer fb) {
        discoveryComp = fb.getBoolean();
        
        ArrayList l = new ArrayList();
        while (fb.getRemaining() > 0) {
            AttributeInfo info = new AttributeInfo();
            info.drain(fb);
            l.add(info);
        }
        attributesInfo = (AttributeInfo[]) l.toArray(new AttributeInfo[0]);
    }

    /**
     * @return the discoveryComp
     */
    public boolean getDiscoveryComp() {
        return discoveryComp;
    }

    /**
     * @param discoveryComp the discoveryComp to set
     */
    public void setDiscoveryComp(boolean discoveryComp) {
        this.discoveryComp = discoveryComp;
    }

    /**
     * @return the attrInfo
     */
    public AttributeInfo[] getAttributesInfo() {
        return attributesInfo;
    }

    /**
     * @param attributesInfo the attrInfo to set
     */
    public void setAttributesInfo(AttributeInfo[] attributesInfo) {
        this.attributesInfo = attributesInfo;
    }

    public static class AttributeInfo implements Frame {
        private short attributeId;
        private byte dataType;
        
        public void pull(FrameBuffer fb) {
            fb.putInt16(attributeId);
            fb.putInt8(dataType);
        }

        public int quote() {
            return 3;
        }

        public void drain(FrameBuffer fb) {
            attributeId = fb.getShort();
            dataType = fb.getByte();
        }

        /**
         * @return the attributeId
         */
        public short getAttributeId() {
            return attributeId;
        }

        /**
         * @param attributeId the attributeId to set
         */
        public void setAttributeId(short attributeId) {
            this.attributeId = attributeId;
        }

        /**
         * @return the dataType
         */
        public byte getDataType() {
            return dataType;
        }

        /**
         * @param dataType the dataType to set
         */
        public void setDataType(byte dataType) {
            this.dataType = dataType;
        }
    }
}