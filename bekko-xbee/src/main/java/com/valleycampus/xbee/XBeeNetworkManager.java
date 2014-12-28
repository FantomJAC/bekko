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
package com.valleycampus.xbee;

import com.valleycampus.xbee.api.XBeeAPI;
import com.valleycampus.xbee.api.XBeeIO;
import com.valleycampus.zigbee.IEEEAddress;
import com.valleycampus.zigbee.NetworkAddress;
import com.valleycampus.zigbee.io.ByteUtil;
import com.valleycampus.zigbee.zdo.NetworkManager;
import com.valleycampus.zigbee.zdp.AddressTable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class XBeeNetworkManager implements NetworkManager, AddressTable {
    
    private final Map addressMap;
    private final Object addressLock = new Object();
    private final XBeeIO xbIO;
    private IEEEAddress ieeeAddress = null;
    
    public XBeeNetworkManager(XBeeIO xbIO) {
        this.xbIO = xbIO;
        addressMap = new HashMap();
    }
    
    public void update(IEEEAddress address64, NetworkAddress address16, int capability) {
        synchronized (addressLock) {
            addressMap.put(address64, address16);
        }
    }

    public NetworkAddress lookupNodeIdByEui64(IEEEAddress address64) {
        synchronized (addressLock) {
            Object o = addressMap.get(address64);
            if (o != null) {
                return (NetworkAddress) o;
            }
        }
        return null;
    }

    public IEEEAddress lookupEui64ByNodeId(NetworkAddress address16) {
        synchronized (addressLock) {
            Set entrySet = addressMap.entrySet();
            for (Iterator it = entrySet.iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry) it.next();
                if (entry.getValue().equals(address16)) {
                    return (IEEEAddress) entry.getKey();
                }
            }
        }
        return null;
    }

    public void permitJoin(int pj) throws IOException {
        if (pj < 0 || 0xFF < pj) {
            throw new IOException("Out of range");
        }
        xbIO.write8("NJ", pj);
        xbIO.write8("CB", (byte) 2);
    }

    public List energyScan(int scanChannels, int scanDuration) throws IOException {
        throw new UnsupportedOperationException("Not supported on XBee.");
    }

    public List networkDiscovery(int scanChannels, int scanDuration) throws IOException {
        throw new UnsupportedOperationException("Not supported on XBee.");
    }

    public void formNetwork(long extPanId, int scanChannels, int scanDuration) throws IOException {
        throw new UnsupportedOperationException("Not supported on XBee.");
    }

    public void leaveNetwork() throws IOException {
        xbIO.write0("DA");
    }
    
    public IEEEAddress getIEEEAddress() {
        if (ieeeAddress == null) {
            try {
                int[] addr64 = new int[2];
                addr64[0] = xbIO.read32("SH");
                addr64[1] = xbIO.read32("SL");
                byte[] ieee = ByteUtil.BIG_ENDIAN.toByteArray(addr64, ByteUtil.INT_32_SIZE, 2);
                ieeeAddress = IEEEAddress.getByAddress(ieee);
            } catch (IOException ex) {
                throw new IllegalAccessError("Can't Read PhysicalAddress." + ex);
            }
        }
        return ieeeAddress;
    }

    public NetworkAddress getNetworkAddress() throws IOException {
        int my = xbIO.read16("MY");
        return NetworkAddress.getByAddress(ByteUtil.BIG_ENDIAN.toByteArray(my, ByteUtil.INT_16_SIZE));
    }
    
    public int getCurrentChannel() throws IOException {
        return xbIO.read8("CH");
    }

    public long getCurrentExtendedPANID() throws IOException {
        return xbIO.read64("OP");
    }

    public int getCurrentPANID() throws IOException {
        return xbIO.read16("OI");
    }

    public int getNodeType() throws IOException {
        int vr = xbIO.read16("VR");
        int type = (vr & XBeeAPI.VR_TYPE_MASK);
        switch (type) {
        case XBeeAPI.VR_COORDINATOR:
            return TYPE_COORDINATOR;
        case XBeeAPI.VR_ROUTER:
            return TYPE_ROUTER;
        case XBeeAPI.VR_END_DEVICE:
            return TYPE_END_DEVICE;
        default:
            return -1;
        }
    }
}
