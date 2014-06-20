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
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class DescReq implements ZDPCommand {

    private NetworkAddress networkAddr;
    
    public void pull(FrameBuffer frameBuffer) {
        frameBuffer.putInt16(networkAddr.toShort());
    }

    public int quote() {
        int q = 0;
        q += ByteUtil.INT_16_SIZE;
        return q;
    }

    public void drain(FrameBuffer frameBuffer) {
        networkAddr = NetworkAddress.getByAddress(frameBuffer.getShort());
    }

    /**
     * @return the networkAddr
     */
    public NetworkAddress getNetworkAddr() {
        return networkAddr;
    }

    /**
     * @param networkAddr the networkAddr to set
     */
    public void setNetworkAddr(NetworkAddress networkAddr) {
        this.networkAddr = networkAddr;
    }
}
