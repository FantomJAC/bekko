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
package com.valleycampus.xbee;

import com.valleycampus.ember.shared.EmberDevice;
import com.valleycampus.ember.shared.ZDPContext;
import com.valleycampus.xbee.api.XBeeDriver;
import com.valleycampus.zigbee.zdo.ZigBeeDevice;
import java.io.IOException;
import com.valleycampus.zigbee.apl.BekkoDeviceManager;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class XBeeDeviceManager implements BekkoDeviceManager {

    private DriverManager drvMgr = new DriverManager();
    
    public ZigBeeDevice createSharedZigBeeDevice() throws IOException {
        if (Boolean.getBoolean(XBeeDevice.DEBUG_ZNET)) {
            EmberDevice.setDebugEnabled(true);
        }
        if (Boolean.getBoolean(XBeeDevice.DEBUG_XBEE)) {
            XBeeDriver.setDebugEnabled(true);
            if (Boolean.getBoolean(XBeeDevice.TRACE_XBEE)) {
                XBeeDriver.setTraceEnabled(true);
            }
        }
        if (Boolean.getBoolean(EmberDevice.DEBUG_ZDP)) {
            ZDPContext.setDebugEnabled(true);
        }

        String portName = System.getProperty(XBeeDevice.PORT_NAME);
        if (portName == null) {
            throw new IOException("Null port name");
        }
        try {
            return drvMgr.openDriver(portName);
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    public void releaseSharedZigBeeDevice() throws IOException {
        drvMgr.closeDriver();
    }

}
