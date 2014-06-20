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
 * ZigBee 64bit IEEE Address, also known as IEEE EUI-64.
 * 
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class IEEEAddress extends ZigBeeAddress {
    
    public static final int ADDRESS_SIZE = 8;
    public static final IEEEAddress NULL_ADDRESS = new IEEEAddress(new byte[] {
        (byte) 0x00,
        (byte) 0x00,
        (byte) 0x00,
        (byte) 0x00,
        (byte) 0x00,
        (byte) 0x00,
        (byte) 0x00,
        (byte) 0x00
    });
    
    protected IEEEAddress(byte[] addr64) {
        super(addr64, ADDRESS_SIZE);
    }

    /**
     * Get IEEEAddress by a byte array representation.
     * The byte order of the array must be MSB first.
     * 
     * @param address
     * @return 
     */
    public static IEEEAddress getByAddress(byte[] addr64) {
        return new IEEEAddress(addr64);
    }

    /**
     * Get IEEEAddress by a long representation.
     * 
     * @param address
     * @return 
     */
    public static IEEEAddress getByAddress(long address) {
        byte[] addr64 = new byte[ADDRESS_SIZE];
        for (int p = 0; p < ADDRESS_SIZE; p++) {
            addr64[p] = (byte) ((address >> (8 * (ADDRESS_SIZE - 1 - p))) & 0xffL);
        } 
        return new IEEEAddress(addr64);
    }
    
    /**
     * Returns a long representation of the IEEEAddress.
     * 
     * @return 
     */
    public long toLong() {
        long address = 0;
        for (int p = 0; p < ADDRESS_SIZE; p++) {
            long d = mac[p] & 0xff;
            address |= (d << (8 * (ADDRESS_SIZE - 1 - p)));
        } 
        return address;
    }
}
