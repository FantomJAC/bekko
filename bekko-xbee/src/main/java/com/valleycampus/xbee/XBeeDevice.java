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

import com.valleycampus.ember.shared.EmberDevice;
import com.valleycampus.ember.shared.EmberManagementEntity;
import com.valleycampus.xbee.api.XBeeAPI;
import com.valleycampus.xbee.api.XBeeIO;
import com.valleycampus.zigbee.zdo.CommissioningManager;
import java.io.IOException;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class XBeeDevice extends EmberDevice {
    
    public static final String PORT_NAME = "com.valleycampus.xbee.comPort";
    public static final String DEBUG_XBEE = "com.valleycampus.xbee.debug.driver";
    public static final String TRACE_XBEE = "com.valleycampus.xbee.trace.driver";

    public static final String NAME_XBEE_S2 = "XBee";
    public static final String NAME_XBEE_S2_PRO = "XBee-PRO(S2)";
    public static final String NAME_XBEE_S2B_PRO = "XBee-PRO(S2B)";
    public static final String NAME_XBEE_S2C_PRO = "XBee-PRO(S2C)";

    private XBeeIO xbIO;
    private EmberManagementEntity managementEntity;
    private XBeeSecurityManager securityManager;
    private XBeeCommissioningManager commissioningManager;
    
    protected XBeeDevice(
            XBeeIO xbIO,
            XBeeNetworkManager nwkMgr,
            XBeeDataService dataService,
            XBeeSecurityManager securityMgr) {
        super(nwkMgr, dataService);
        this.xbIO = xbIO;
        this.securityManager = securityMgr;
        this.managementEntity = new EmberManagementEntity();
    }
    
    public static XBeeDevice createXBeeDevice(XBeeAPI xbAPI) throws IOException {
        XBeeIO xbIO = new XBeeIO(xbAPI);
        
        int vr = xbIO.read16("VR");
        String fwVersion = Integer.toHexString(vr);
        EmberDevice.debug("FWVersion: " + fwVersion);

        XBeeSecurityManager securityManager;
        if ((vr & XBeeAPI.VR_FIRM_MASK) == XBeeAPI.VR_SE) {
            securityManager = new XBeeSecurityManager(xbIO, true);
        } else {
            securityManager = new XBeeSecurityManager(xbIO, false);
        }
        
        XBeeNetworkManager nwkMgr = new XBeeNetworkManager(xbIO);
        XBeeDevice device = new XBeeDevice(xbIO, nwkMgr, new XBeeDataService(nwkMgr, new XBeeBindingManager(), xbAPI), securityManager);
        
        if ((vr & XBeeAPI.VR_TYPE_MASK) == XBeeAPI.VR_COORDINATOR) {
            device.getManagementEntity().setAIB(EmberManagementEntity.APS_TRUST_CENTER_ADDRESS, device.getIEEEAddress());
        }
        
        return device;
    }

    public XBeeIO getXBeeIO() {
        return xbIO;
    }
    
    protected EmberManagementEntity getManagementEntity() {
        return managementEntity;
    }
    
    protected XBeeSecurityManager getSecurityManager() {
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
}
