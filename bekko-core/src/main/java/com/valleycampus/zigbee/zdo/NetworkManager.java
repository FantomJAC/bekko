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

import java.io.IOException;
import java.util.List;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public interface NetworkManager {

    public static final byte NWK_SEQUENCE_NUMBER = (byte) 0x81;
    public static final byte NWK_PASSIVE_ACK_TIMEOUT = (byte) 0x82;
    public static final byte NWK_MAX_BROADCAST_RETRIES = (byte) 0x83;
    public static final byte NWK_MAX_CHILDREN = (byte) 0x84;
    public static final byte NWK_MAX_DEPTH = (byte) 0x85;
    public static final byte NWK_MAX_ROUTERS = (byte) 0x86;
    public static final byte NWK_NEIGHBOR_TABLE = (byte) 0x87;
    public static final byte NWK_NETWORK_BROADCAST_DELIVERY_TIME = (byte) 0x88;
    public static final byte NWK_REPORT_CONSTANT_COST = (byte) 0x89;
    public static final byte NWK_ROUTE_TABLE = (byte) 0x8b;
    public static final byte NWK_SYM_LINK = (byte) 0x8e;
    public static final byte NWK_CAPABILITY_INFORMATION = (byte) 0x8f;
    public static final byte NWK_ADDR_ALLOC = (byte) 0x90;
    public static final byte NWK_USE_TREE_ROUTING = (byte) 0x91;
    public static final byte NWK_MANAGER_ADDR = (byte) 0x92;
    public static final byte NWK_MAX_SOURCE_ROUTE = (byte) 0x93;
    public static final byte NWK_UPDATE_ID = (byte) 0x94;
    public static final byte NWK_TRANSACTION_PERSISTENCE_TIME = (byte) 0x95;
    public static final byte NWK_NETWORK_ADDRESS = (byte) 0x96;
    public static final byte NWK_STACK_PROFILE = (byte) 0x97;
    public static final byte NWK_BROADCAST_TRANSACTION_TABLE = (byte) 0x98;
    public static final byte NWK_GROUP_ID_TABLE = (byte) 0x99;
    public static final byte NWK_EXTENDED_PAN_ID = (byte) 0x9a;
    public static final byte NWK_USE_MULTICAST = (byte) 0x9b;
    public static final byte NWK_ROUTE_RECORD_TABLE = (byte) 0x9c;
    public static final byte NWK_IS_CONCENTRATOR = (byte) 0x9d;
    public static final byte NWK_CONCENTRATOR_RADIUS = (byte) 0x9e;
    public static final byte NWK_CONCENTRATOR_DISCOVERY_TIME = (byte) 0x9f;
    public static final byte NWK_SECURITY_LEVEL = (byte) 0xa0;
    public static final byte NWK_SECURITY_MATERIAL_SET = (byte) 0xa1;
    public static final byte NWK_ACTIVE_KEY_SEQ_NUMBER = (byte) 0xa2;
    public static final byte NWK_ALL_FRESH = (byte) 0xa3;
    public static final byte NWK_SECURE_ALL_FRAMES = (byte) 0xa5;
    public static final byte NWK_LINK_STATUS_PERIOD = (byte) 0xa6;
    public static final byte NWK_ROUTER_AGE_LIMIT = (byte) 0xa7;
    public static final byte NWK_UNIQUE_ADDR = (byte) 0xa8;
    public static final byte NWK_ADDRESS_MAP = (byte) 0xa9;
    public static final byte NWK_TIME_STAMP = (byte) 0x8c;
    public static final byte NWK_PAN_ID = (byte) 0x80;
    public static final byte NWK_TX_TOTAL = (byte) 0x8D;
    public static final byte NWK_LEAVE_REQUEST_ALLOWED = (byte) 0xAA;
    
    /**
     * Read the value of an attribute from the NIB. (NLME-GET.request)
     * @param attribute
     * @return 
     * @throws java.io.IOException 
     */
    public Object get(byte attribute) throws IOException;
    
    /**
     * Write the value of an attribute into the NIB. (NLME-GET.request)
     * @param attribute
     * @param value
     * @return 
     * @throws java.io.IOException 
     */
    public boolean set(byte attribute, Object value) throws IOException;
    
    public List networkDiscovery(int scanChannels, int scanDuration) throws IOException;
    
    public void leaveNetwork() throws IOException;

    public byte getNetworkStatus() throws IOException;
}
