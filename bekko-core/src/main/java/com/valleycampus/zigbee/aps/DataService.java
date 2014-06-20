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
package com.valleycampus.zigbee.aps;

import java.io.IOException;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public interface DataService {
    
    public static final byte ADDRESS_MODE_NULL = 0x00;
    public static final byte ADDRESS_MODE_GROUP = 0x01;
    public static final byte ADDRESS_MODE_NWK = 0x02;
    public static final byte ADDRESS_MODE_EUI64 = 0x03;
    
    public int getMaxAPDUSize() throws IOException;
    
    public byte transmit(DataRequest request) throws IOException;
    
    public void addDataReceiver(DataReceiver receiver);
    
    public void removeDataReceiver(DataReceiver receiver);
}
