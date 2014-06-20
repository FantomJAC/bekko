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

import java.net.ProtocolException;

/**
 * Abstract ZigBee addressing primitive.
 * 
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public abstract class ZigBeeAddress {

    protected int addressSize;
    protected byte[] mac;

    protected ZigBeeAddress(byte[] mac, int addressSize) {
        this.addressSize = addressSize;
        if (mac == null) {
            this.mac = new byte[addressSize];
            for (int i = 0; i < addressSize; i++) {
                this.mac[i] = 0x00;
            }
        } else {
            this.mac = mac;
        }
    }
    
    /**
     * Get ZigBeeAddress by string representation.
     * <p>
     * The string will be interpreted as either {@link NetworkAddress} or
     * {@link IEEEAddress}.
     * The representation should be unsigned 16-bit or 64-bit integer,
     * in hexadecimal form, zero-padded and without leading '0x' or trailing 'h'.
     * </p>
     * 
     * @param address
     * @return IEEEAddress or NetworkAddress.
     * @throws ProtocolException 
     */
    public static ZigBeeAddress getByAddress(String address) throws ProtocolException {
        if ((address.length() % 2) != 0) {
            throw new ProtocolException("Invalid address.");
        }

        int addressSize = (address.length() / 2);
        
        byte[] mac = new byte[addressSize];
        for (int i = 0; i < addressSize; i++) {
            int p = (i * 2);
            mac[i] = (byte) Integer.parseInt(address.substring(p, p + 2), 16);
        }
        
        switch (addressSize) {
        case IEEEAddress.ADDRESS_SIZE:
            return new IEEEAddress(mac);
        case NetworkAddress.ADDRESS_SIZE:
            return new NetworkAddress(mac);
        }
        
        throw new ProtocolException("Invalid address.");
    }
    
    /**
     * Check if the address is an unicast address.
     * 
     * @return true if the address is an unicast address.
     */
    public boolean isUnicast() {
        return !(this instanceof GroupAddress)
                && !(
                    (this instanceof NetworkAddress)
                    && ((NetworkAddress) this).isBroadcast());
    }

    public String toString() {
        String s = "";
        for (int i = 0; i < addressSize; i++) {
            s += toHexString(mac[i]);
        }
        return s;
    }

    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + hashcode(mac);
        return hash;
    }
    
    private static String toHexString(byte b) {
        String hex = Integer.toHexString(b);
        int len = hex.length();
        if (len == 2) {
            return hex;
        } else if (len == 1) {
            return "0" + hex;
        } else {
            return hex.substring(len - 2);
        }
    }
    
    private static int hashcode(byte[] a) {
        if (a == null) {
            return 0;
        }

        int hash = 1;
        for (int i = 0; i < a.length; i++) {
            hash = 31 * hash + a[i];
        }
        
        return hash;
    }

    public boolean equals(Object obj) {
        if (obj instanceof ZigBeeAddress) {
            ZigBeeAddress target = (ZigBeeAddress) obj;
            if (target.addressSize != this.addressSize) {
                return false;
            }
            for (int i = 0; i < addressSize; i++) {
                if (target.mac[i] != this.mac[i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Returns the raw byte array of the address.
     * The byte order of the array is MSB first.
     * 
     * @return 
     */
    public byte[] array() {
        return mac;
    }
}
