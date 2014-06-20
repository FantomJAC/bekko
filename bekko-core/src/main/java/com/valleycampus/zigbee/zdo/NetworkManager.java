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
package com.valleycampus.zigbee.zdo;

import com.valleycampus.zigbee.NetworkAddress;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public interface NetworkManager {
    
    public void permitJoin(int pj) throws IOException;
    
    public List energyScan(int scanChannels, int scanDuration) throws IOException;
    
    public List networkDiscovery(int scanChannels, int scanDuration) throws IOException;
    
    public void formNetwork(long extPanId, int scanChannels, int scanDuration) throws IOException;
    
    public void leaveNetwork() throws IOException;
    
    public NetworkAddress getNetworkAddress() throws IOException;
}