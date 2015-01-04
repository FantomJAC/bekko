/*
 * Copyright (C) 2014 Shotaro Uchida
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
package com.valleycampus.xbee;

import com.valleycampus.xbee.api.XBeeAPI;
import com.valleycampus.xbee.api.XBeeIO;
import com.valleycampus.zigbee.GroupAddress;
import com.valleycampus.zigbee.ZigBeeAddress;
import com.valleycampus.zigbee.ZigBeeConst;
import com.valleycampus.zigbee.aps.ManagementService;
import java.io.IOException;

/**
 *
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public class XBeeManagementService implements ManagementService {

    private final XBeeIO xbIO;
    private final XBeeBindingManager bindingManager;
    
    public XBeeManagementService(XBeeIO xbIO, XBeeBindingManager bindingManager) {
        this.xbIO = xbIO;
        this.bindingManager = bindingManager;
    }
    
    public byte bind(int srcEndpoint, short clusterId, ZigBeeAddress dstAddress, int dstEndpoint) {
        return bindingManager.bind(new BindInfo(srcEndpoint, clusterId, dstAddress, dstEndpoint));
    }

    public byte unbind(int srcEndpoint, short clusterId, ZigBeeAddress dstAddress, int dstEndpoint) {
        return bindingManager.unbind(new BindInfo(srcEndpoint, clusterId, dstAddress, dstEndpoint));
    }

    public Object get(byte attribute) throws IOException {
        switch (attribute) {
        case APS_DESIGNATED_COORDINATOR:
            return ((xbIO.read16("VR") & XBeeAPI.VR_TYPE_MASK) == XBeeAPI.VR_COORDINATOR) ?
                Boolean.TRUE : Boolean.FALSE;
        case APS_CHANNEL_MASK:
            return Integer.valueOf((xbIO.read16("SC") & 0xFFFF) << 11);
        case APS_USE_EXTENDED_PAN_ID:
            return Long.valueOf(xbIO.read64("ID"));
        }
        return null;
    }

    public boolean set(byte attribute, Object value) throws IOException {
        switch (attribute) {
        case APS_CHANNEL_MASK:
            xbIO.write16("SC", ((Number) value).shortValue());
            break;
        case APS_USE_EXTENDED_PAN_ID:
            xbIO.write64("ID", ((Number) value).longValue());
            break;
        default:
            return false;
        }
        return true;
    }

    public byte addGroup(GroupAddress groupAddress, int endpoint) {
        return ZigBeeConst.NOT_SUPPORTED;
    }

    public byte removeGroup(GroupAddress groupAddress, int endpoint) {
        return ZigBeeConst.NOT_SUPPORTED;
    }

    public byte removeAllGroup(int endpoint) {
        return ZigBeeConst.NOT_SUPPORTED;
    }
}
