/*
 * Copyright (C) 2014 Shotaro Uchida
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

import com.valleycampus.zigbee.GroupAddress;
import com.valleycampus.zigbee.ZigBeeAddress;
import java.io.IOException;

/**
 *
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public interface ManagementService {
    
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
    
    /**
     * Add a binding record to local binding table. (APSME-BIND.request)
     * @param srcEndpoint Endpoint wishes to be bound.
     * @param clusterId
     * @param dstAddress
     * @param dstEndpoint will be ignored when the dstAddress is Group address.
     * @return Confirmed status.
     * @throws java.io.IOException
     */
    public byte bind(int srcEndpoint,
                     short clusterId,
                     ZigBeeAddress dstAddress,
                     int dstEndpoint) throws IOException;
    
    /**
     * Remove a binding record from local binding table. (APSME-UNBIND.request)
     * @param srcEndpoint Endpoint wishes to be unbound.
     * @param clusterId
     * @param dstAddress
     * @param dstEndpoint will be ignored when the dstAddress is Group address.
     * @return Confirmed status.
     * @throws java.io.IOException
     */
    public byte unbind(int srcEndpoint,
                       short clusterId,
                       ZigBeeAddress dstAddress,
                       int dstEndpoint) throws IOException;
    
    /**
     * Read the value of an attribute from the AIB. (APSME-GET.request)
     * @param attribute
     * @return 
     * @throws java.io.IOException 
     */
    public Object get(byte attribute) throws IOException;
    
    /**
     * Write the value of an attribute into the AIB. (APSME-GET.request)
     * @param attribute
     * @param value
     * @return 
     * @throws java.io.IOException 
     */
    public boolean set(byte attribute, Object value) throws IOException;
    
    /**
     * Add a group to an endpoint. (APSME-ADD-GROUP.request)
     * @param groupAddress
     * @param endpoint
     * @return Confirmed status.
     */
    public byte addGroup(GroupAddress groupAddress, int endpoint);
    
    /**
     * Remove a group from an endpoint. (APSME-REMOVE-GROUP.request)
     * @param groupAddress
     * @param endpoint
     * @return Confirmed status.
     */
    public byte removeGroup(GroupAddress groupAddress, int endpoint);
    
    /**
     * Remove all groups from an endpoint. (APSME-REMOVE-ALL-GROUP.request)
     * @param endpoint
     * @return Confirmed status.
     */
    public byte removeAllGroup(int endpoint);
}
