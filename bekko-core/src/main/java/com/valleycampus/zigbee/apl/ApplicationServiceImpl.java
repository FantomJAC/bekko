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
package com.valleycampus.zigbee.apl;

import com.valleycampus.zigbee.ApplicationException;
import com.valleycampus.zigbee.ApplicationService;
import com.valleycampus.zigbee.ZigBeeDataConnection;
import com.valleycampus.zigbee.ZigBeeSimpleDescriptor;
import com.valleycampus.zigbee.zdo.ZigBeeDevice;
import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Properties;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import com.valleycampus.zigbee.util.Commons;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class ApplicationServiceImpl implements ApplicationService {

    public static final String APPLICATION_COUNT = "zigbee.application.count";
    
    private ZigBeeDevice device;
    private HashMap applicationMap;
    private HashMap propertiesMap;
    
    public ApplicationServiceImpl(ZigBeeDevice device) {
        this.device = device;
        applicationMap = new HashMap();
        propertiesMap = new HashMap();
    }
    
    private void debug(String str) {
        System.out.println("[ApplicationService] " + str);
    }

    public void registerEndpoint(ZigBeeSimpleDescriptor simpleDescriptor, Dictionary properties) throws ApplicationException {
        synchronized (this) {
            int endpoint = simpleDescriptor.getEndpoint();
            if (endpoint < 0x01 || 0xF0 < endpoint) {
                throw new ApplicationException("Invalid endpoint.");
            }
            Object key = new Integer(endpoint);
            if (applicationMap.containsKey(key)) {
                throw new ApplicationException("Endpoint has been registered already.");
            }
            applicationMap.put(key, simpleDescriptor);
            propertiesMap.put(key, properties);
        }
    }

    public void unregisterEndpoint(int endpoint) throws ApplicationException {
        synchronized (this) {
            Object key = new Integer(endpoint);
            applicationMap.remove(key);
            propertiesMap.remove(key);
        }
    }
    
    public void loadEndpoints(Properties properties) {
        int count = Integer.parseInt(properties.getProperty(APPLICATION_COUNT, "0"));
        for (int app = 0; app < count; app++) {
            debug("Loading application#" + app);
            ZigBeeSimpleDescriptor simpleDescriptor = new ZigBeeSimpleDescriptorImpl(properties, app);
            try {
                registerEndpoint(simpleDescriptor, null);
            } catch (ApplicationException ex) {
                debug("Failed to load application#" + app + ": " + ex.getMessage());
            }
        }
    }
    
    /**
     * Connection factory method for javax.microedition.io.Connector.
     * 
     * @param name
     * @param mode
     * @param timeouts
     * @return
     * @throws IOException 
     */
    public Connection open(String name, int mode, boolean timeouts) throws IOException {
        if (mode != Connector.READ_WRITE) {
            throw new IOException("Only Read/Write mode is allowed.");
        }
        int index = name.indexOf("://");
        if (index < 0) {
            throw new IOException("Malformed URI.");
        }
        String scheme = name.substring(0, index);
        if (!scheme.equals("bekko")) {
            throw new IOException("Invalid Scheme: " + scheme);
        }
        String[] s = Commons.split(name.substring(index + 3), ':');
        if (s.length != 2 || !s[0].equals("")) {
            throw new IOException("Malformed URI.");
        }
        int endpoint = Integer.parseInt(s[1], 16);
        return openConnection(endpoint);
    }
    
    protected ZigBeeDataConnection openConnection(int endpoint) throws IOException {
        ZigBeeSimpleDescriptor simpleDescriptor;
        synchronized (this) {
            simpleDescriptor = (ZigBeeSimpleDescriptor) applicationMap.get(new Integer(endpoint));
        }
        if (simpleDescriptor == null) {
            throw new IOException("Endpoint" + endpoint + " is not registered.");
        }
        ZigBeeDataConnectionImpl connImpl = new ZigBeeDataConnectionImpl();
        connImpl.open(device, simpleDescriptor);
        return connImpl;
    }
}
