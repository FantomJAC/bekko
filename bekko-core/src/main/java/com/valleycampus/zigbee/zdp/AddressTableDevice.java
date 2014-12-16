/*
 * Copyright (C) 2014 Valley Campus Japan, Inc.
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
package com.valleycampus.zigbee.zdp;

import com.valleycampus.zigbee.IEEEAddress;
import com.valleycampus.zigbee.NetworkAddress;
import com.valleycampus.zigbee.aps.DataService;
import com.valleycampus.zigbee.zdo.DiscoveryListener;
import com.valleycampus.zigbee.zdo.NetworkManager;
import java.io.IOException;

/**
 * ZigBee Device with external address table interface
 * 
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public abstract class AddressTableDevice extends AbstractZigBeeDevice implements  DiscoveryListener {
    
    private final AddressTable addrTable;
    
    protected AddressTableDevice(NetworkManager nwkMgr, DataService dataService, AddressTable addressTable) {
        super(nwkMgr, dataService);
        this.addrTable = addressTable;
        addDiscoveryListener(this);
    }

    public NetworkAddress lookupNodeIdByEui64(IEEEAddress eui64) throws IOException {
        return addrTable.lookupNodeIdByEui64(eui64);
    }

    public IEEEAddress lookupEui64ByNodeId(NetworkAddress nodeId) throws IOException {
        return addrTable.lookupEui64ByNodeId(nodeId);
    }

    public void deviceAnnounce(NetworkAddress nwkAddress, IEEEAddress ieeeAddress, byte capability) {
        addrTable.update(ieeeAddress, nwkAddress, (capability & 0xFF));
    }

    public void deviceMatched(int tsn, NetworkAddress nwkAddress, int[] matchList) { }

    public void deviceDiscovered(IEEEAddress ieeeAddress, NetworkAddress nwkAddress) {
        addrTable.update(ieeeAddress, nwkAddress, -1);
    }
    
}
