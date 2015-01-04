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
package com.valleycampus.zigbee.zdp;

import com.valleycampus.zigbee.ZigBeeAddress;
import com.valleycampus.zigbee.ZigBeeConst;
import com.valleycampus.zigbee.ZigBeeSimpleDescriptor;
import com.valleycampus.zigbee.aps.DataService;
import com.valleycampus.zigbee.aps.ManagementService;
import com.valleycampus.zigbee.io.Frame;
import com.valleycampus.zigbee.zdo.DiscoveryListener;
import com.valleycampus.zigbee.zdo.NetworkManager;
import com.valleycampus.zigbee.zdo.ZDPCommandPacket;
import com.valleycampus.zigbee.zdo.ZigBeeDevice;
import com.valleycampus.zigbee.zdp.command.ZDPCommand;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public abstract class AbstractZigBeeDevice implements ZigBeeDevice {

	private static final int MAX_ENDPOINT = 0xF0;
	private static final int MIN_ENDPOINT = 0x01;
	private final NetworkManager nwkMgr;
	private final DataService dataService;
    private final ManagementService mgmtService;
	private final DiscoveryService discovery;
	private final ZDPContext zdpContext;
	private final HashMap endpointMap;
	private final Object endpointLock = new Object();

	protected AbstractZigBeeDevice(NetworkManager nwkMgr, DataService dataService, ManagementService mgmtService) {
		this.nwkMgr = nwkMgr;
		this.dataService = dataService;
        this.mgmtService = mgmtService;

		zdpContext = new ZDPContext(dataService);
        dataService.addDataReceiver(zdpContext);
        
		discovery = new DiscoveryService(this, nwkMgr, zdpContext);
        zdpContext.registerZDPServerService(discovery);

		endpointMap = new HashMap();
	}

	public boolean activate() {
		return discovery.activate();
	}

	public boolean shutdown() {
		return discovery.shutdown();
	}

	public NetworkManager getNetworkManager() {
		return nwkMgr;
	}

	public DataService getDataService() {
		return dataService;
	}

    public ManagementService getManagementService() {
        return mgmtService;
    }
    
	public int addEndpoint(ZigBeeSimpleDescriptor simpleDescriptor) {
		if (simpleDescriptor == null) {
			throw new IllegalArgumentException("SimpleDescriptor cannot be null");
		}
		synchronized (endpointLock) {
			int endpoint = validateEndpoint(simpleDescriptor.getEndpoint());
			endpointMap.put(new Integer(endpoint), ZigBeeSimpleDescriptorImpl.clone(endpoint, simpleDescriptor));
			return endpoint;
		}
	}

	private int validateEndpoint(int endpoint) {
		if (endpoint != ZigBeeConst.BRODCAST_ENDPOINT) {
			if (endpoint < MIN_ENDPOINT || MAX_ENDPOINT < endpoint) {
				throw new IllegalArgumentException("Invalid endpoint.");
			} else if (endpointMap.containsKey(new Integer(endpoint))) {
				throw new IllegalArgumentException("Endpoint has been activated already.");
			}
			return endpoint;
		} else {
			for (int ep = MIN_ENDPOINT; ep <= MAX_ENDPOINT; ep++) {
				if (!endpointMap.containsKey(new Integer(ep))) {
					return ep;
				}
			}
			throw new IllegalArgumentException("All endpoints are activated.");
		}
	}

	public void removeEndpoint(int endpoint) {
		synchronized (endpointLock) {
			Object key = new Integer(endpoint);
			endpointMap.remove(key);
		}
	}

	public int[] getActiveEndpoints() {
		synchronized (endpointLock) {
			int[] endpoints = new int[endpointMap.size()];
			int i = 0;
			for (Iterator it = endpointMap.keySet().iterator(); it.hasNext();) {
				endpoints[i++] = ((Integer) it.next()).intValue();
			}
			return endpoints;
		}
	}

	public ZigBeeSimpleDescriptor getSimpleDescriptor(int endpoint) {
		synchronized (endpointLock) {
			Object val = endpointMap.get(new Integer(endpoint));
			if (val != null) {
				return (ZigBeeSimpleDescriptor) val;
			}
			return null;
		}
	}

	public void addDiscoveryListener(DiscoveryListener listener) {
		discovery.addDiscoveryListener(listener);
	}

	public boolean removeDiscoveryListener(DiscoveryListener listener) {
		return discovery.removeDiscoveryListener(listener);
	}

    public int sendZDPCommand(ZigBeeAddress address, short clusterId, Frame command, int txOptions, int radius) throws IOException {
        return zdpContext.sendCommand(address, clusterId, command, txOptions, radius, -1);
    }

	public ZDPCommandPacket sendZDPCommandAndWait(ZigBeeAddress address, short clusterId, Frame command, int txOptions, int radius, long timeout) throws IOException {
		return zdpContext.sendCommandSync(address, clusterId, command, txOptions, radius, timeout);
	}
    
    public ZDPCommandPacket sendZDPCommand(ZigBeeAddress address, short clusterId, ZDPCommand command, int txOptions, int radius, long timeout) throws IOException {
		return sendZDPCommandAndWait(address, clusterId, command, txOptions, radius, timeout);
	}
}
