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

import com.valleycampus.zigbee.IEEEAddress;
import com.valleycampus.zigbee.NetworkAddress;
import com.valleycampus.zigbee.io.FrameBuffer;
import com.valleycampus.zigbee.io.ByteUtil;

/**
 *
 * @author Eisuke Yasato <eyasato@valleycampus.com>
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class DeviceAnnounce implements ZDPCommand {

    public static final byte CAP_ALT_PAN_COORDINATOR = 0x01;
    public static final byte CAP_DEVICE_TYPE = 0x02;
    public static final byte CAP_POWER_SOURCE = 0x04;
    public static final byte CAP_RECEIVER_ON_WHEN_IDLE = 0x08;
    public static final byte CAP_SECURITY_CAPABILITY = 0x40;
    public static final byte CAP_ALLOCATE_ADDRESS = (byte) 0x80;
    private IEEEAddress addr64;
    private NetworkAddress addr16;
    private byte capability;

    public void pull(FrameBuffer frameBuffer) {
        frameBuffer.putInt16(addr16.toShort());
        frameBuffer.putInt64(addr64.toLong());
        frameBuffer.put(capability);
    }

    public int quote() {
        int q = 0;
        q += ByteUtil.INT_64_SIZE;
        q += ByteUtil.INT_16_SIZE;
        q += ByteUtil.INT_8_SIZE;
        return q;
    }

    public void drain(FrameBuffer frameBuffer) {
        this.addr16 = NetworkAddress.getByAddress(frameBuffer.getShort());
        this.addr64 = IEEEAddress.getByAddress(frameBuffer.getInt64());
        this.capability = frameBuffer.getByte();
    }

    /**
     * @return the addr64
     */
    public IEEEAddress getAddr64() {
        return addr64;
    }

    /**
     * @param addr64 the addr64 to set
     */
    public void setAddr64(IEEEAddress addr64) {
        this.addr64 = addr64;
    }

    /**
     * @return the addr16
     */
    public NetworkAddress getAddr16() {
        return addr16;
    }

    /**
     * @param addr16 the addr16 to set
     */
    public void setAddr16(NetworkAddress addr16) {
        this.addr16 = addr16;
    }

    /**
     * @return the capability
     */
    public byte getCapability() {
        return capability;
    }

    /**
     * @param capability the capability to set
     */
    public void setCapability(byte capability) {
        this.capability = capability;
    }
}
