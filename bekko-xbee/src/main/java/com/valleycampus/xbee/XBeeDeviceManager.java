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

import com.valleycampus.xbee.api.RXTXDriverContext;
import com.valleycampus.xbee.api.XBeeAPI;
import com.valleycampus.xbee.api.XBeeDriver;
import com.valleycampus.xbee.api.XBeeIO;
import com.valleycampus.xbee.digimesh.DigiMeshDevice;
import com.valleycampus.zigbee.zdo.ZigBeeDevice;
import java.io.IOException;
import com.valleycampus.zigbee.apl.BekkoDeviceManager;
import com.valleycampus.zigbee.zdp.AbstractZigBeeDevice;
import com.valleycampus.zigbee.zdp.ZDPContext;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class XBeeDeviceManager implements BekkoDeviceManager {

    private RXTXDriverContext drvCtx;
    private AbstractZigBeeDevice zdo;
    
    public ZigBeeDevice createSharedZigBeeDevice() throws IOException {
        synchronized (this) {
            if (zdo != null) {
                return zdo;
            }
        }
        
        if (Boolean.getBoolean(XBeeDevice.DEBUG_ZNET)) {
            XBeeDevice.setDebugEnabled(true);
        }
        if (Boolean.getBoolean(XBeeDevice.DEBUG_XBEE_DRIVER)) {
            XBeeDriver.setDebugEnabled(true);
            if (Boolean.getBoolean(XBeeDevice.TRACE_XBEE_DRIVER)) {
                XBeeDriver.setTraceEnabled(true);
            }
        }
        if (Boolean.getBoolean(XBeeDevice.DEBUG_ZDP)) {
            ZDPContext.setDebugEnabled(true);
        }

        String portName = System.getProperty(XBeeDevice.PORT_NAME);
        if (portName == null) {
            throw new IOException("Null port name");
        }
        try {
            RXTXDriverContext ctx = RXTXDriverContext.openDriver(portName);
            synchronized (this) {
                drvCtx = ctx;
                zdo = createZigBeeDevice(drvCtx.getXBeeAPI());
                zdo.activate();
            }
            return zdo;
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    public void releaseSharedZigBeeDevice() throws IOException {
        synchronized (this) {
            if (zdo != null) {
                zdo.shutdown();
                zdo = null;
                drvCtx.closeDriver();
            }
        }
    }
    
    private static AbstractZigBeeDevice createZigBeeDevice(XBeeAPI xbAPI) throws IOException {
        XBeeIO xbIO = new XBeeIO(xbAPI);
        xbIO.setTimeout(3000);

        int hv = xbIO.read16("HV");
        switch (hv & XBeeAPI.HV_MASK) {
        case XBeeAPI.HV_S2:
        case XBeeAPI.HV_S2_PRO:
        case XBeeAPI.HV_S2B_PRO:
        case 0x2100:
        case 0x2200:
            XBeeDevice.debug("ZigBee(S2) device is detected.");
            return XBeeDevice.createXBeeDevice(xbAPI);
        case 0x2300:
            XBeeDevice.debug("DigiMesh(S3) device is detected.");
        case 0x2400:
            XBeeDevice.debug("DigiMesh(S8) device is detected.");
            return DigiMeshDevice.createDigiMeshDevice(xbAPI);
        default:
            throw new IOException("Module " + Integer.toHexString(hv) + " not support ZigBee");
        }
    }
}
