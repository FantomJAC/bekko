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

import com.valleycampus.ember.shared.EmberDevice;
import com.valleycampus.xbee.api.XBeeAPI;
import com.valleycampus.xbee.api.XBeeIO;
import com.valleycampus.zigbee.zdo.CommissioningManager;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class DigiMeshDevice extends EmberDevice {
    
    private XBeeIO xbIO;
    private DigiMeshCommissioningManager commissioningManager;
    
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

}
