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

import com.valleycampus.xbee.api.XBeeAPI;
import com.valleycampus.xbee.api.XBeeIO;
import com.valleycampus.zigbee.IEEEAddress;
import com.valleycampus.zigbee.NetworkAddress;
import com.valleycampus.zigbee.io.ByteUtil;
import com.valleycampus.zigbee.zdo.CommissioningManager;
import com.valleycampus.zigbee.zdp.AbstractZigBeeDevice;
import java.io.IOException;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class DigiMeshDevice extends AbstractZigBeeDevice {
    
    private final XBeeIO xbIO;
    private final DigiMeshCommissioningManager commissioningManager;
    private IEEEAddress ieeeAddress = null;
    
    protected DigiMeshDevice(XBeeIO xbIO, DigiMeshNetworkManager nwkMgr, DigiMeshDataService dataService) {
        super(nwkMgr, dataService);
        this.xbIO = xbIO;
        this.commissioningManager = new DigiMeshCommissioningManager(xbIO);
    }

    public static DigiMeshDevice createDigiMeshDevice(XBeeAPI xbAPI) {
        XBeeIO xbIO = new XBeeIO(xbAPI);
        DigiMeshNetworkManager nwkMgr = new DigiMeshNetworkManager(xbIO);
        return new DigiMeshDevice(xbIO, nwkMgr, new DigiMeshDataService(xbAPI));
    }
    
    public byte getNetworkStatus() {
        return 0;
    }

    public CommissioningManager getCommissioningManager() {
        return commissioningManager;
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

    public NetworkAddress lookupNodeIdByEui64(IEEEAddress eui64) throws IOException {
        return NetworkAddress.BROADCAST_MROWI;
    }

    public IEEEAddress lookupEui64ByNodeId(NetworkAddress nodeId) throws IOException {
        throw new IOException("Unsupported on the DigiMesh device.");
    }
}
