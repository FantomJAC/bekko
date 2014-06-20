/*
 * Copyright (C) 2013 Valley Campus Japan, Inc.
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

import com.valleycampus.zigbee.ApplicationService;
import com.valleycampus.zigbee.zdo.ZigBeeDevice;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.microedition.io.Connection;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class BekkoConnector {
    
    public static final String APP_PROPERTIES = "com.valleycampus.zigbee.appProperties";
    
    private static BekkoDeviceManager deviceMgr;
    private static ZigBeeDevice sharedDevice;
    private static ApplicationServiceImpl sharedImpl;
    private static final Object lock = new Object();
    
    public static ApplicationService getSharedApplicationService() {
        synchronized (lock) {
            return sharedImpl;
        }
    }
    
    public static ZigBeeDevice getSharedZigBeeDevice() {
        synchronized (lock) {
            return sharedDevice;
        }
    }
    
    public static Connection open(String name, int mode, boolean timeouts) throws IOException {
        synchronized (lock) {
            if (deviceMgr == null) {
                String className = System.getProperty(BekkoDeviceManager.CLASS);
                if (className == null) {
                    throw new IOException("No BekkoDeviceManager impl.");
                }
                try {
                    Class clazz = Class.forName(className);
                    deviceMgr = (BekkoDeviceManager) clazz.newInstance();
                } catch (Exception ex) {
                    throw new IOException(ex);
                }
            }
            
            if (sharedDevice == null) {
                sharedDevice = deviceMgr.createSharedZigBeeDevice();
            }
            
            if (sharedImpl == null) {
                sharedImpl = new ApplicationServiceImpl(sharedDevice);
                String propName = System.getProperty(APP_PROPERTIES);
                if (propName != null) {
                    try {
                        Properties appProperties = new Properties();
                        appProperties.load(new FileInputStream(propName));
                        sharedImpl.loadEndpoints(appProperties);
                    } catch (Exception ex) {
                        System.out.println("[BekkoConnector] Can't load applications: " + propName);
                    }
                }
            }
            
            return sharedImpl.open(name, mode, timeouts);
        }
    }
}
