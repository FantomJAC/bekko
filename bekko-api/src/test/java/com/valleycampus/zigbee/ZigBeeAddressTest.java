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
import java.util.Arrays;
import junit.framework.TestCase;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class ZigBeeAddressTest extends TestCase {
    
    public ZigBeeAddressTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testIsUnicast() throws ProtocolException {
        System.out.println("isUnicast");
        assertTrue(IEEEAddress.getByAddress("00137a0000006487").isUnicast());
        assertFalse(NetworkAddress.BROADCAST_MROWI.isUnicast());
        assertFalse(ZigBeeAddress.getByAddress("fffd").isUnicast());
        assertTrue(ZigBeeAddress.getByAddress("1234").isUnicast());
        assertFalse(GroupAddress.getByAddress((short) 0x1234).isUnicast());
    }

    /**
     * Test of toString method, of class ZigBeeAddress.
     */
    public void testToString() throws ProtocolException {
        System.out.println("toString");
        assertEquals(IEEEAddress.getByAddress(0x00137a0000006487L).toString(), "00137a0000006487");
    }

    /**
     * Test of equals method, of class ZigBeeAddress.
     */
    public void testEquals() throws ProtocolException {
        System.out.println("equals");
        IEEEAddress eui64 = IEEEAddress.getByAddress(0x00137a0000006487L);
        assertTrue(ZigBeeAddress.getByAddress("00137a0000006487").equals(eui64));
    }

    /**
     * Test of array method, of class ZigBeeAddress.
     */
    public void testArray() throws ProtocolException {
        System.out.println("array");

        byte[] eui64Exp = new byte[] {
            (byte) 0x00, (byte) 0x13, (byte) 0x7a, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x64, (byte) 0x87};
        assertTrue(Arrays.equals(eui64Exp, IEEEAddress.getByAddress(0x00137a0000006487L).array()));
        assertTrue(Arrays.equals(eui64Exp, IEEEAddress.getByAddress(eui64Exp).array()));
        assertTrue(Arrays.equals(eui64Exp, ZigBeeAddress.getByAddress("00137a0000006487").array()));
        
        byte[] nwkExp = new byte[] {(byte) 0x12, (byte) 0x34};
        assertTrue(Arrays.equals(nwkExp, NetworkAddress.getByAddress((short) 0x1234).array()));
        assertTrue(Arrays.equals(nwkExp, NetworkAddress.getByAddress(nwkExp).array()));
        assertTrue(Arrays.equals(nwkExp, ZigBeeAddress.getByAddress("1234").array()));
    }
}
