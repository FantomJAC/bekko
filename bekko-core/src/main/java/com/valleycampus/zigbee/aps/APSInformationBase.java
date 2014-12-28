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
package com.valleycampus.zigbee.aps;

import com.valleycampus.zigbee.IEEEAddress;
import java.io.IOException;
import java.util.Hashtable;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public final class APSInformationBase {
    
    public static final byte APS_BINDING_TABLE = (byte) 0xc1;
    public static final byte APS_DESIGNATED_COORDINATOR = (byte) 0xc2;
    public static final byte APS_CHANNEL_MASK = (byte) 0xc3;
    public static final byte APS_USE_EXTENDED_PAN_ID = (byte) 0xc4;
    public static final byte APS_GROUP_TABLE = (byte) 0xc5;
    public static final byte APS_NONMEMBER_RADIUS = (byte) 0xc6;
    public static final byte APS_PERMISSION_CONFIGURATION = (byte) 0xc7;
    public static final byte APS_USE_INSECURE_JOIN = (byte) 0xc8;
    public static final byte APS_INTERFRAME_DELAY = (byte) 0xc9;
    public static final byte APS_LAST_CHANNEL_ENERGY = (byte) 0xca;
    public static final byte APS_LAST_CHANNEL_FAILURE_RATE = (byte) 0xcb;
    public static final byte APS_CHANNEL_TIMER = (byte) 0xcc;
    public static final byte APS_DEVICE_KEY_PAIR_SET = (byte) 0xaa;
    public static final byte APS_TRUST_CENTER_ADDRESS = (byte) 0xab;
    public static final byte APS_SECURITY_TIMEOUT_PERIOD = (byte) 0xac;
    
    private Hashtable stdIBTable;
    
    public APSInformationBase() {
        stdIBTable = new Hashtable();
        initialize();
    }
    
    private void initialize() {
        try {
            setAIB(APS_DESIGNATED_COORDINATOR, Boolean.FALSE);
            setAIB(APS_CHANNEL_MASK, new Integer(0x07FFF800));
            setAIB(APS_USE_EXTENDED_PAN_ID, new Long(0x0000000000000000));
            setAIB(APS_NONMEMBER_RADIUS, new Integer(2));
            //setAIB(APS_TRUST_CENTER_ADDRESS, null);
        } catch (IOException ex) {
            System.out.println("[APS] Failed to initialize AIB");
        }
    }
    
    private void checkLong(Object ib, long min, long max) throws IOException {
        if (!(ib instanceof Long)) {
            throw new IOException("The ib must be Long");
        }
        Long l = (Long) ib;
        if (l.longValue() < min || max < l.longValue()) {
            throw new IllegalArgumentException("Invalid value");
        }
    }

    private void checkInteger(Object ib, int min, int max) throws IOException {
        if (!(ib instanceof Integer)) {
            throw new IOException("The ib must be Integer");
        }
        Integer i = (Integer) ib;
        if (i.intValue() < min || max < i.intValue()) {
            throw new IllegalArgumentException("Invalid value");
        }
    }
    
    private void checkBoolean(Object ib) throws IOException {
        if (!(ib instanceof Boolean)) {
            throw new IOException("The ib must be Boolean");
        }
    }
    
    public long getAIBAsLong(byte identifier) throws IOException {
        return ((Number) getAIB(identifier)).longValue();
    }
    
    public int getAIBAsInteger(byte identifier) throws IOException {
        return ((Number) getAIB(identifier)).intValue();
    }
    
    public boolean getAIBAsBoolean(byte identifier) throws IOException {
        return ((Boolean) getAIB(identifier)).booleanValue();
    }
    
    public Object getAIB(byte identifier) throws IOException {
        switch (identifier) {
        case APS_DESIGNATED_COORDINATOR:
        case APS_CHANNEL_MASK:
        case APS_USE_EXTENDED_PAN_ID:
        case APS_NONMEMBER_RADIUS:
        case APS_TRUST_CENTER_ADDRESS:
            return stdIBTable.get(new Byte(identifier));
        default:
            throw new IOException("Unsupported AIB");
        }  
    }
    
    private void setAIBToTable(byte identifier, Object ib) {
        stdIBTable.put(new Byte(identifier), ib);
    }
    
    public void setAIB(byte identifier, Object ib) throws IOException {
        switch (identifier) {
        case APS_DESIGNATED_COORDINATOR:
            checkBoolean(ib);
            setAIBToTable(identifier, ib);
            break;
        case APS_CHANNEL_MASK:
            setAIBToTable(identifier, ib);
            break;
        case APS_USE_EXTENDED_PAN_ID:
            if (!(ib instanceof Long)) {
                throw new IOException("The ib must be Long");
            }
            Long l = (Long) ib;
            if (l.longValue() == 0xFFFFFFFFFFFFFFFFL) {
                throw new IllegalArgumentException("Invalid PANID");
            }
            setAIBToTable(identifier, ib);
            break;
        case APS_NONMEMBER_RADIUS:
            checkInteger(ib, 0, 7);
            setAIBToTable(identifier, ib);
            break;
        case APS_TRUST_CENTER_ADDRESS:
            if (!(ib instanceof IEEEAddress)) {
                throw new IOException("The ib must be IEEEAddress");
            }
            setAIBToTable(identifier, ib);
            break;
        default:
            throw new IOException("Unsupported AIB");
        }
    }
}
