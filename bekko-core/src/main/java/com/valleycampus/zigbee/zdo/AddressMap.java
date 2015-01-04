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
package com.valleycampus.zigbee.zdo;

import com.valleycampus.zigbee.IEEEAddress;
import com.valleycampus.zigbee.NetworkAddress;
import java.io.IOException;

/**
 *
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public interface AddressMap {
    
    public NetworkAddress lookupNodeIdByEui64(IEEEAddress eui64) throws IOException;
    
    public IEEEAddress lookupEui64ByNodeId(NetworkAddress nodeId) throws IOException;
}
