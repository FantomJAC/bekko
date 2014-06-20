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
package com.valleycampus.xbee.digimesh;

import com.valleycampus.ember.shared.EmberNetworkManager;
import com.valleycampus.xbee.api.XBeeIO;
import com.valleycampus.zigbee.IEEEAddress;
import com.valleycampus.zigbee.NetworkAddress;
import java.io.IOException;
import java.util.List;
import com.valleycampus.zigbee.io.ByteUtil;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class DigiMeshNetworkManager implements EmberNetworkManager {
    
    private XBeeIO xbIO;
    private IEEEAddress ieeeAddress = null;
    
    public DigiMeshNetworkManager(XBeeIO xbIO) {
        this.xbIO = xbIO;
    }
    
    public void aggregateRouteRequest(IEEEAddress address) throws IOException {
        xbIO.write64("AG", address.toLong());
    }

    public NetworkAddress lookupNodeIdByEui64(IEEEAddress eui64) throws IOException {
        return NetworkAddress.BROADCAST_MROWI;
    }

    public IEEEAddress lookupEui64ByNodeId(NetworkAddress nodeId) throws IOException {
        throw new IOException("Unsupported on the DigiMesh device.");
    }

    public void updateAddressMap(IEEEAddress senderEUI64, NetworkAddress sender, boolean sleepyAnnounce) {
        // Nothing.
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
        return NetworkAddress.BROADCAST_MROWI;
    }

    public int getNodeType() throws IOException {
        return TYPE_ROUTER;
    }

    public void permitJoin(int i) throws IOException {
        throw new IOException("Unsupported on the DigiMesh device.");
    }

    public List energyScan(int i, int i1) throws IOException {
        throw new IOException("Unsupported on the DigiMesh device.");
    }

    public List networkDiscovery(int i, int i1) throws IOException {
        throw new IOException("Unsupported on the DigiMesh device.");
    }

    public void formNetwork(long l, int i, int i1) throws IOException {
        throw new IOException("Unsupported on the DigiMesh device.");
    }

    public void leaveNetwork() throws IOException {
        throw new IOException("Unsupported on the DigiMesh device.");
    }

}
