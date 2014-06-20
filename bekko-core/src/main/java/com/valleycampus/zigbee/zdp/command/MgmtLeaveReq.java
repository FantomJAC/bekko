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
import com.valleycampus.zigbee.io.FrameBuffer;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class MgmtLeaveReq implements ZDPCommand {
    
    private IEEEAddress deviceAddress;
    private boolean removeChildren;
    private boolean rejoin;

    public void pull(FrameBuffer frameBuffer) {
        frameBuffer.setByteOrder(FrameBuffer.BO_LITTLE_ENDIAN);
        if (deviceAddress == null) {
            frameBuffer.put(IEEEAddress.NULL_ADDRESS.array());
        } else {
            frameBuffer.putInt64(deviceAddress.toLong());
        }
        int cap = 0;
        cap |= (removeChildren ? 0x02 : 0);
        cap |= (rejoin ? 0x01 : 0);
        frameBuffer.putInt8(cap);
    }

    public int quote() {
        return 9;
    }

    public void drain(FrameBuffer frameBuffer) {
        frameBuffer.setByteOrder(FrameBuffer.BO_LITTLE_ENDIAN);
        deviceAddress = IEEEAddress.getByAddress(frameBuffer.getByteArray(IEEEAddress.ADDRESS_SIZE));
        int cap = frameBuffer.getInt8();
        removeChildren = (cap & 0x02) > 0;
        rejoin = (cap & 0x01) > 0;
    }
    
    /**
     * @return the deviceAddress
     */
    public IEEEAddress getDeviceAddress() {
        return deviceAddress;
    }

    /**
     * @param deviceAddress the deviceAddress to set
     */
    public void setDeviceAddress(IEEEAddress deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    /**
     * @return the removeChildren
     */
    public boolean isRemoveChildren() {
        return removeChildren;
    }

    /**
     * @param removeChildren the removeChildren to set
     */
    public void setRemoveChildren(boolean removeChildren) {
        this.removeChildren = removeChildren;
    }

    /**
     * @return the rejoin
     */
    public boolean isRejoin() {
        return rejoin;
    }

    /**
     * @param rejoin the rejoin to set
     */
    public void setRejoin(boolean rejoin) {
        this.rejoin = rejoin;
    }
}
