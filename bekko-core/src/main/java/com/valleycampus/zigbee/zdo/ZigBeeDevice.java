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

import com.valleycampus.zigbee.IEEEAddress;
import com.valleycampus.zigbee.ZigBeeAddress;
import com.valleycampus.zigbee.ZigBeeException;
import com.valleycampus.zigbee.ZigBeeSimpleDescriptor;
import com.valleycampus.zigbee.aps.DataService;
import com.valleycampus.zigbee.aps.ManagementService;
import com.valleycampus.zigbee.io.Frame;
import java.io.IOException;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public interface ZigBeeDevice {

    public static final byte STATUS_SUCCESS = 0x00;
    public static final byte STATUS_INV_REQUEST_TYPE = (byte) 0x80;
    public static final byte STATUS_DEVICE_NOT_FOUND = (byte) 0x81;
    public static final byte STATUS_INVALID_EP = (byte) 0x82;
    public static final byte STATUS_NOT_ACTIVE = (byte) 0x83;
    public static final byte STATUS_NOT_SUPPORTED = (byte) 0x84;
    public static final byte STATUS_TIMEOUT = (byte) 0x85;
    public static final byte STATUS_NO_MATCH = (byte) 0x86;
    public static final byte STATUS_NO_ENTRY = (byte) 0x88;
    public static final byte STATUS_NO_DESCRIPTOR = (byte) 0x89;
    public static final byte STATUS_INSUFFICIENT_SPACE = (byte) 0x8a;
    public static final byte STATUS_NOT_PERMITTED = (byte) 0x8b;
    public static final byte STATUS_TABLE_FULL = (byte) 0x8c;
    public static final byte STATUS_NOT_AUTHORIZED = (byte) 0x8d;
    
	public static final int TYPE_COORDINATOR = 0x01;
    public static final int TYPE_ROUTER = 0x02;
    public static final int TYPE_END_DEVICE = 0x03;

    public int addEndpoint(ZigBeeSimpleDescriptor simpleDescriptor) throws IOException;
    
    public void removeEndpoint(int endpoint) throws IOException;
    
    public int[] getActiveEndpoints();
    
    public ZigBeeSimpleDescriptor getSimpleDescriptor(int endpoint);
    
    public void addDiscoveryListener(DiscoveryListener listener);
    
    public boolean removeDiscoveryListener(DiscoveryListener listener);

    public int sendZDPCommand(
            ZigBeeAddress address,
            short clusterId,
            Frame command,
            int txOptions,
            int radius) throws ZigBeeException, IOException;
    
    public ZDPCommandPacket sendZDPCommandAndWait(
            ZigBeeAddress address,
            short clusterId,
            Frame command,
            int txOptions,
            int radius,
            long timeout) throws ZigBeeException, IOException;
    
    public NetworkManager getNetworkManager();
    
    public IEEEAddress getIEEEAddress();
    
    public int getNodeType() throws IOException;
    
    public DataService getDataService();
    
    public ManagementService getManagementService();
}
