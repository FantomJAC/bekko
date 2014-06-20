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
public class MatchDescRsp extends DescRsp {

    private byte matchLength;
    private int[] matchList;

    public void pull(FrameBuffer frameBuffer) {
        frameBuffer.setByteOrder(FrameBuffer.BO_LITTLE_ENDIAN);
        frameBuffer.put(getStatus());
        frameBuffer.putInt16(getNetworkAddr().toShort());
        frameBuffer.put(matchLength);
        for (int i = 0; i < matchLength; i++) {
            frameBuffer.putInt8(matchList[i]);
        }
    }

    public int quote() {
        int q = 0;
        q += ByteUtil.INT_8_SIZE;
        q += ByteUtil.INT_16_SIZE;
        q += ByteUtil.INT_8_SIZE;
        q += matchLength * ByteUtil.BYTE_SIZE;

        return q;
    }

    public void drain(FrameBuffer frameBuffer) {
        frameBuffer.setByteOrder(FrameBuffer.BO_LITTLE_ENDIAN);
        setStatus(frameBuffer.getByte());
        setNetworkAddr(NetworkAddress.getByAddress(frameBuffer.getShort()));
        matchLength = frameBuffer.getByte();
        matchList = new int[matchLength];
        for(int i = 0; i < matchLength; i++) {
            matchList[i] = frameBuffer.getInt8();
        }
    }

    /**
     * @return the matchLength
     */
    public byte getMatchLength() {
        return matchLength;
    }

    /**
     * @param matchLength the matchLength to set
     */
    public void setMatchLength(byte matchLength) {
        this.matchLength = matchLength;
    }

    /**
     * @return the matchList
     */
    public int[] getMatchList() {
        return matchList;
    }

    /**
     * @param matchList the matchList to set
     */
    public void setMatchList(int[] matchList) {
        this.matchList = matchList;
    }
}
