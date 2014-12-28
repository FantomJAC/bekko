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
package com.valleycampus.xbee;

import com.valleycampus.xbee.api.XBeeAPI;
import com.valleycampus.xbee.api.XBeeIO;
import com.valleycampus.zigbee.IEEEAddress;
import com.valleycampus.zigbee.aps.APSInformationBase;
import com.valleycampus.zigbee.zdo.CommissioningManager;
import com.valleycampus.zigbee.zdp.AddressTableDevice;
import java.io.IOException;
import java.io.PrintStream;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class XBeeDevice extends AddressTableDevice {
    
    public static final String PORT_NAME = "com.valleycampus.xbee.comPort";
    public static final String DEBUG_ZNET = "com.valleycampus.xbee.debug.znet";
    public static final String DEBUG_ZDP = "com.valleycampus.xbee.debug.zdp";
    public static final String DEBUG_XBEE_DRIVER = "com.valleycampus.xbee.debug.driver";
    public static final String TRACE_XBEE_DRIVER = "com.valleycampus.xbee.trace.driver";
    
    public static final String NAME_XBEE_S2 = "XBee";
    public static final String NAME_XBEE_S2_PRO = "XBee-PRO(S2)";
    public static final String NAME_XBEE_S2B_PRO = "XBee-PRO(S2B)";
    public static final String NAME_XBEE_S2C_PRO = "XBee-PRO(S2C)";

    private final XBeeIO xbIO;
    private final APSInformationBase managementEntity;
    private final XBeeSecurityManager securityManager;
    private XBeeCommissioningManager commissioningManager;

    private static PrintStream logStream = System.out;
    private static volatile boolean debugEnabled;
    
    protected XBeeDevice(
            XBeeIO xbIO,
            XBeeNetworkManager nwkMgr,
            XBeeDataService dataService,
            XBeeSecurityManager securityMgr) {
        super(nwkMgr, dataService, nwkMgr);
        this.xbIO = xbIO;
        this.securityManager = securityMgr;
        this.managementEntity = new APSInformationBase();
    }
    
    public static void setLogStream(PrintStream logStream) {
        XBeeDevice.logStream = logStream;
    }
    
    public static void setDebugEnabled(boolean debugEnabled) {
        XBeeDevice.debugEnabled = debugEnabled;
    }

    public static void debug(String str) {
        if (debugEnabled) {
            logStream.println("[ZNet#debug] " + str);
        }
    }

    public static void warn(String str) {
        logStream.println("[ZNet#warn] " + str);
    }
    
    public static XBeeDevice createXBeeDevice(XBeeAPI xbAPI) throws IOException {
        XBeeIO xbIO = new XBeeIO(xbAPI);
        
        int vr = xbIO.read16("VR");
        String fwVersion = Integer.toHexString(vr);
        XBeeDevice.debug("FWVersion: " + fwVersion);

        XBeeSecurityManager securityManager;
        if ((vr & XBeeAPI.VR_FIRM_MASK) == XBeeAPI.VR_SE) {
            securityManager = new XBeeSecurityManager(xbIO, true);
        } else {
            securityManager = new XBeeSecurityManager(xbIO, false);
        }
        
        XBeeNetworkManager nwkMgr = new XBeeNetworkManager(xbIO);
        XBeeDevice device = new XBeeDevice(xbIO, nwkMgr, new XBeeDataService(nwkMgr, new XBeeBindingManager(), xbAPI), securityManager);
        
        if ((vr & XBeeAPI.VR_TYPE_MASK) == XBeeAPI.VR_COORDINATOR) {
            device.getManagementEntity().setAIB(APSInformationBase.APS_TRUST_CENTER_ADDRESS, device.getIEEEAddress());
        }
        
        return device;
    }

    public XBeeIO getXBeeIO() {
        return xbIO;
    }
    
    public APSInformationBase getManagementEntity() {
        return managementEntity;
    }
    
    public XBeeSecurityManager getSecurityManager() {
        return securityManager;
    }

    public String getFWVersion() {
        try {
            return Integer.toHexString(xbIO.read16("VR"));
        } catch (IOException ex) {
            return "UnknownFW";
        }
    }
    
    public String getHWVersion() {
        try {
            int hv = xbIO.read16("HV");
            switch (hv & XBeeAPI.HV_MASK) {
            case XBeeAPI.HV_S2:
                return NAME_XBEE_S2;
            case XBeeAPI.HV_S2_PRO:
                return NAME_XBEE_S2_PRO;
            case XBeeAPI.HV_S2B_PRO:
                return NAME_XBEE_S2B_PRO;
            case 0x2100:
            case 0x2200:
                return NAME_XBEE_S2_PRO;
            default:
                return Integer.toHexString(hv);
            }
        } catch (IOException ex) {
            return "UnknownHW";
        }
    }

    public String toString() {
        return getHWVersion() + " - " + getFWVersion();
    }

    public byte getNetworkStatus() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public CommissioningManager getCommissioningManager() {
        if (commissioningManager == null) {
            commissioningManager = new XBeeCommissioningManager(this, managementEntity);
        }
        return commissioningManager;
    }

    public IEEEAddress getIEEEAddress() {
        return ((XBeeNetworkManager) getNetworkManager()).getIEEEAddress();
    }
}
