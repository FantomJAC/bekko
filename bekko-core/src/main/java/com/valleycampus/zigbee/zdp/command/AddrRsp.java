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
import java.util.ArrayList;
import java.util.List;
import com.valleycampus.zigbee.io.ByteUtil;
import com.valleycampus.zigbee.io.FrameBuffer;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class AddrRsp implements ZDPCommand {

    private byte status;
    private IEEEAddress ieeeAddress;
    private NetworkAddress networkAddress;
    private int numAssocDev;
    private int startIndex;
    private List deviceList;

    public void pull(FrameBuffer frameBuffer) {
        frameBuffer.put(status);
        frameBuffer.putInt64(ieeeAddress.toLong());
        frameBuffer.putInt16(networkAddress.toShort());
        if (numAssocDev > 0) {
            frameBuffer.putInt8(numAssocDev);
            if (deviceList != null && !deviceList.isEmpty()) {
                frameBuffer.putInt8(startIndex);
                for (int i = 0; i < deviceList.size(); i++) {
                    frameBuffer.putInt16(((NetworkAddress) deviceList.get(i)).toShort());
                }
            }
        }
    }

    public int quote() {
        int q = 0;
        q += ByteUtil.INT_8_SIZE;
        q += ByteUtil.INT_64_SIZE;
        q += ByteUtil.INT_16_SIZE;
        if (numAssocDev > 0) {
            q += ByteUtil.INT_8_SIZE;
            if (deviceList != null && !deviceList.isEmpty()) {
                q += ByteUtil.INT_8_SIZE;
                q += ByteUtil.INT_16_SIZE * deviceList.size();
            }
        }
        return q;
    }

    public void drain(FrameBuffer frameBuffer) {
        status = frameBuffer.get();
        ieeeAddress = IEEEAddress.getByAddress(frameBuffer.getInt64());
        networkAddress = NetworkAddress.getByAddress(frameBuffer.getShort());
        if (frameBuffer.getRemaining() > 0) {
            numAssocDev = frameBuffer.getInt8();
            if (numAssocDev > 0) {
                startIndex = frameBuffer.getInt8();
                deviceList = new ArrayList();
                while (frameBuffer.getRemaining() > 0) {
                    deviceList.add(NetworkAddress.getByAddress(frameBuffer.getShort()));
                }
            }
        }
    }
    
    /**
     * @return the status
     */
    public byte getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(byte status) {
        this.status = status;
    }

    /**
     * @return the ieeeAddress
     */
    public IEEEAddress getIEEEAddress() {
        return ieeeAddress;
    }

    /**
     * @param ieeeAddress the ieeeAddress to set
     */
    public void setIEEEAddress(IEEEAddress ieeeAddress) {
        this.ieeeAddress = ieeeAddress;
    }

    /**
     * @return the networkAddress
     */
    public NetworkAddress getNetworkAddress() {
        return networkAddress;
    }

    /**
     * @param networkAddress the networkAddress to set
     */
    public void setNetworkAddress(NetworkAddress networkAddress) {
        this.networkAddress = networkAddress;
    }
    
    /**
     * @return the numAssocDev
     */
    public int getNumAssocDev() {
        return numAssocDev;
    }

    /**
     * @param numAssocDev the numAssocDev to set
     */
    public void setNumAssocDev(int numAssocDev) {
        this.numAssocDev = numAssocDev;
    }

    /**
     * @return the startIndex
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * @param startIndex the startIndex to set
     */
    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    /**
     * @return the deviceList
     */
    public List getDeviceList() {
        return deviceList;
    }

    /**
     * @param deviceList the deviceList to set
     */
    public void setDeviceList(List deviceList) {
        this.deviceList = deviceList;
    }
}
