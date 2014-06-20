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
package com.valleycampus.zigbee;

/**
 * ZigBee Group Address
 * 
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class GroupAddress extends ZigBeeAddress {
    
    public static final int ADDRESS_SIZE = 2;
    
    protected GroupAddress(byte[] addr16) {
        super(addr16, ADDRESS_SIZE);
    }
    
    /**
     * Get GroupAddress by a byte array representation.
     * The byte order of the array must be MSB first.
     * 
     * @param address
     * @return 
     */
    public static GroupAddress getByAddress(byte[] address) {
        return new GroupAddress(address);
    }
    
    /**
     * Get GroupAddress by a short representation.
     * 
     * @param address
     * @return 
     */
    public static GroupAddress getByAddress(short address) {
        byte[] addr16 = new byte[ADDRESS_SIZE];
        addr16[1] = (byte) (address & 0xFF);
        addr16[0] = (byte) ((address >> 8) & 0xFF);
        return new GroupAddress(addr16);
    }

    /**
     * Returns a short representation of the GroupAddress.
     * 
     * @return 
     */
    public short toShort() {
        return (short) ((mac[0] & 0xFF) << 8 | (mac[1] & 0xFF));
    }
}
