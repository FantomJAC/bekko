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
import com.valleycampus.zigbee.io.FrameBuffer;
import com.valleycampus.zigbee.io.ByteUtil;

/**
 *
 * @author Eisuke Yasato <eyasato@valleycampus.com>
 */
public class ActiveEpRsp extends DescRsp {

    private int[] activeEpList;

    public void pull(FrameBuffer frameBuffer) {
        frameBuffer.setByteOrder(FrameBuffer.BO_LITTLE_ENDIAN);
        frameBuffer.put(getStatus());
        frameBuffer.putInt16(getNetworkAddr().toShort());
        int epCount = 0;
        if (activeEpList != null) {
            epCount = activeEpList.length;
        }
        frameBuffer.putInt8(epCount);
        for (int i = 0; i < epCount; i++) {
            frameBuffer.putInt8(activeEpList[i]);
        }
    }

    public int quote() {
        int q = super.quote();
        q += ByteUtil.INT_8_SIZE;
        if (activeEpList != null) {
            q += activeEpList.length * ByteUtil.INT_8_SIZE;
        }
        return q;
    }

    public void drain(FrameBuffer frameBuffer) {
        frameBuffer.setByteOrder(FrameBuffer.BO_LITTLE_ENDIAN);
        setStatus(frameBuffer.getByte());
        setNetworkAddr(NetworkAddress.getByAddress(frameBuffer.getShort()));
        int epCount = frameBuffer.getInt8();
        activeEpList = new int[epCount];
        for(int i = 0; i < epCount; i++) {
            activeEpList[i] = frameBuffer.getInt8();
        }
    }

    /**
     * @return the activeEpList
     */
    public int[] getActiveEpList() {
        return activeEpList;
    }

    /**
     * @param activeEpList the activeEpList to set
     */
    public void setActiveEpList(int[] activeEpList) {
        this.activeEpList = activeEpList;
    }
}
