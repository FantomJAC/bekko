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
import com.valleycampus.ember.shared.EmberNetworkManager;
import com.valleycampus.xbee.api.XBeeIO;
import com.valleycampus.zigbee.IEEEAddress;
import com.valleycampus.zigbee.zdo.CommissioningManager;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class XBeeCommissioningManager implements CommissioningManager {

    public static final byte[] HA_TC_LINK_KEY = new byte[] {
        0x5A, 0x69, 0x67, 0x42, 0x65, 0x65, 0x41, 0x6C, 0x6C, 0x69, 0x61, 0x6E, 0x63,
        0x65, 0x30, 0x39
    };
    public static final int DEFAULT_SCAN_CHANNELS = 0x1FFE;
    public static final int DEFAULT_SCAN_DURATION = 3;
    public static final int DEFAULT_NODE_JOIN_TIME = 30;
    public static final int DEFAULT_ZIGBEE_STACK = 2;
    public static final int DEFAULT_SECURITY_LEVEL = 5;
    public static final int SC_CONNECTED = 0x00;
    public static final int SC_FORM = 0x01;
    public static final int SC_REJOIN = 0x02;
    public static final int SC_JOIN_ANY = 0x03;
    
    private XBeeIO xbIO;
    private XBeeNetworkManager nwkMgr;
    private XBeeSecurityManager securityMgr;
    private EmberManagementEntity mgmtEntity;
    private Dictionary parameters;
    
    public XBeeCommissioningManager(XBeeDevice zdo, EmberManagementEntity mgmtEntity) {
        this.xbIO = zdo.getXBeeIO();
        this.nwkMgr = (XBeeNetworkManager) zdo.getNetworkManager();
        this.securityMgr = zdo.getSecurityManager();
        this.mgmtEntity = mgmtEntity;
        parameters = new Hashtable();
    }

    public void updateParameter(Object key, Object val) {
        parameters.put(key, val);
    }
    
    public void updateParameters(Dictionary parameters) {
        this.parameters = parameters;
    }

    public void commission() throws IOException {
        int sc = ((Integer) parameters.get(SAS_STARTUP_CONTROL)).intValue();
        switch (sc) {
        case SC_FORM:
            mgmtEntity.setAIB(EmberManagementEntity.APS_DESIGNATED_COORDINATOR, Boolean.TRUE);
            mgmtEntity.setAIB(
                    EmberManagementEntity.APS_USE_EXTENDED_PAN_ID,
                    parameters.get(SAS_EXTENDED_PAN_ID));
            
            Object panId = parameters.get(SAS_PAN_ID);
            if (panId != null) {
                // XXX: PANId should be ignored? NWK may pick the PAN.
            }
            
            Object channelMask = parameters.get(SAS_CHANNEL_MASK);
            if (channelMask != null) {
                mgmtEntity.setAIB(EmberManagementEntity.APS_CHANNEL_MASK, channelMask);
            }
            
            Object trustCenterAddress = parameters.get(SAS_TRUST_CENTER_ADDRESS);
            if (trustCenterAddress != null) {
                long addr = ((Long) trustCenterAddress).longValue();
                mgmtEntity.setAIB(EmberManagementEntity.APS_TRUST_CENTER_ADDRESS, IEEEAddress.getByAddress(addr));
            }
            break;
        default:
            throw new UnsupportedOperationException("Not supported yet");
        }
        
        startupDevice();
    }
    
    public void startupDevice() throws IOException {
        EmberDevice.debug("Startup Procedure...");
        
        if (mgmtEntity.getAIBAsBoolean(EmberManagementEntity.APS_DESIGNATED_COORDINATOR)) {
            if (nwkMgr.getNodeType() != EmberNetworkManager.TYPE_COORDINATOR) {
                throw new UnsupportedOperationException("The firmware on this XBee is not Coordinator.");
            }
            EmberDevice.debug("Designated Coordainator");
            // Request forming
            xbIO.setQueueing(true);
            xbIO.write64("ID", mgmtEntity.getAIBAsLong(EmberManagementEntity.APS_USE_EXTENDED_PAN_ID));
            xbIO.write16("SC", mgmtEntity.getAIBAsInteger(EmberManagementEntity.APS_CHANNEL_MASK) >>> 11);
            xbIO.write8("SD", DEFAULT_SCAN_DURATION);
            if (DEFAULT_SECURITY_LEVEL > 0) {
                EmberDevice.debug("Security Level: " + DEFAULT_SECURITY_LEVEL);
                SecurityInfo securityInfo = new SecurityInfo();
                Object tc = mgmtEntity.getAIB(EmberManagementEntity.APS_TRUST_CENTER_ADDRESS);
                if (tc != null && !tc.equals(nwkMgr.getIEEEAddress())) {
                    throw new UnsupportedOperationException("Setting TC as non-coordinator is not supported on XBee.");
                }
                securityInfo.setTrustCenter(true);
                // Use Random NWK Key
                securityInfo.setNetworkKey(new byte[] {0x00});
                // Use HA TC Link Key
                securityInfo.setLinkKey(HA_TC_LINK_KEY);
                securityMgr.initSecurity(securityInfo);
            } else {
                EmberDevice.debug("No Security");
            }
            Object zs = parameters.get(SAS_STACK_PROFILE);
            if (zs == null) {
                xbIO.write8("ZS", DEFAULT_ZIGBEE_STACK);
            } else {
                xbIO.write8("ZS", ((Integer) zs).intValue());
            }
            xbIO.write8("NJ", DEFAULT_NODE_JOIN_TIME);
            xbIO.write8("AO", 3);
            xbIO.applyChanges();
            xbIO.write();
            return;
        }
        
        long extPanId = mgmtEntity.getAIBAsLong(EmberManagementEntity.APS_USE_EXTENDED_PAN_ID);
        if (extPanId != 0) {
            EmberDevice.debug("apsUseExtendedPANID has a non-zero value");
            EmberDevice.debug("TODO: Rejoin attempt");
        } else {
            EmberDevice.debug("apsUseExtendedPANID is equals to 0x0000000000000000");
            EmberDevice.debug("TODO: Join any attempt");
        }
    }
}
