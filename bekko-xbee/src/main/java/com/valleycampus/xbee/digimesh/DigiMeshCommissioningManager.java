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
import com.valleycampus.xbee.api.XBeeIO;
import com.valleycampus.zigbee.zdo.CommissioningManager;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class DigiMeshCommissioningManager implements CommissioningManager {

    private XBeeIO xbIO;
    private Dictionary parameters;
    
    public DigiMeshCommissioningManager(XBeeIO xbIO) {
        this.xbIO = xbIO;
        parameters = new Hashtable();
    }
    
    public void updateParameter(Object key, Object val) {
        parameters.put(key, val);
    }
    
    public void updateParameters(Dictionary parameters) {
        this.parameters = parameters;
    }

    public void commission() throws IOException {
        Object panId = parameters.get(SAS_PAN_ID);
        if (panId != null) {
            EmberDevice.debug("Update Network ID");
            xbIO.write16("ID", ((Number) panId).shortValue());
            xbIO.write();
        }
    }
    
}
