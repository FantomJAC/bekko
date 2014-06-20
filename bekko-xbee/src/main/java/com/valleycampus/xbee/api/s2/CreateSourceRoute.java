/*
 * Copyright (C) 2013 Valley Campus Japan, Inc.
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
package com.valleycampus.xbee.api.s2;

import com.valleycampus.zigbee.io.ByteUtil;
import com.valleycampus.zigbee.io.FrameBuffer;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class CreateSourceRoute extends XBeeAddressingRequest {
    
    private byte routeCommandOptions;
    private short[] addresses;
    
    public CreateSourceRoute() {
        setFrameType(API_CREATE_SOURCE_ROUTE);
    }
    
    public void pull(FrameBuffer frameBuffer) {
        super.pull(frameBuffer);
        frameBuffer.put(getRouteCommandOptions());
        if (getAddresses() != null) {
            frameBuffer.putInt8(getAddresses().length);
            for (int i = 0; i < getAddresses().length; i++) {
                frameBuffer.putInt16(getAddresses()[i]);
            }
        } else {
            frameBuffer.putInt8(0);
        }
    }
    
    public int quote() {
        int q = super.quote() + 2;
        if (getAddresses() != null) {
            q += getAddresses().length * ByteUtil.INT_16_SIZE;
        }
        return q;
    }

    /**
     * @return the routeCommandOptions
     */
    public byte getRouteCommandOptions() {
        return routeCommandOptions;
    }

    /**
     * @param routeCommandOptions the routeCommandOptions to set
     */
    public void setRouteCommandOptions(byte routeCommandOptions) {
        this.routeCommandOptions = routeCommandOptions;
    }

    /**
     * @return the addresses
     */
    public short[] getAddresses() {
        return addresses;
    }

    /**
     * @param addresses the addresses to set
     */
    public void setAddresses(short[] addresses) {
        this.addresses = addresses;
    }
    
}
