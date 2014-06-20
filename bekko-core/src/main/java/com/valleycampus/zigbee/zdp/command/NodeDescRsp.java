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
package com.valleycampus.zigbee.zdp.command;

import com.valleycampus.zigbee.NetworkAddress;
import com.valleycampus.zigbee.ZigBeeConst;
import com.valleycampus.zigbee.io.FrameBuffer;

/**
 *
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public class NodeDescRsp extends DescRsp {
    
    private NodeDescriptorFrame nodeDescriptor;

    public void pull(FrameBuffer frameBuffer) {
        frameBuffer.setByteOrder(FrameBuffer.BO_LITTLE_ENDIAN);
        frameBuffer.put(getStatus());
        frameBuffer.putInt16(getNetworkAddr().toShort());
        if (nodeDescriptor != null) {
            nodeDescriptor.pull(frameBuffer);
        }
    }

    public int quote() {
        int q = super.quote();
        if (nodeDescriptor != null) {
            q += nodeDescriptor.quote();
        }
        return q;
    }

    public void drain(FrameBuffer frameBuffer) {
        frameBuffer.setByteOrder(FrameBuffer.BO_LITTLE_ENDIAN);
        setStatus(frameBuffer.getByte());
        setNetworkAddr(NetworkAddress.getByAddress(frameBuffer.getShort()));
        if (getStatus() == ZigBeeConst.SUCCESS) {
            nodeDescriptor = new NodeDescriptorFrame();
            nodeDescriptor.drain(frameBuffer);
        }
    }

    /**
     * @return the nodeDescriptor
     */
    public NodeDescriptorFrame getNodeDescriptor() {
        return nodeDescriptor;
    }

    /**
     * @param nodeDescriptor the nodeDescriptor to set
     */
    public void setNodeDescriptor(NodeDescriptorFrame nodeDescriptor) {
        this.nodeDescriptor = nodeDescriptor;
    }
}
