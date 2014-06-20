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
import java.util.Dictionary;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public interface CommissioningManager {
    
    public static final String SAS = "com.valleycampus.zigbee.zdo.sas";
    public static final String SAS_SHORT_ADDRESS = SAS + ".ShortAddress";
    public static final String SAS_EXTENDED_PAN_ID = SAS + ".ExtendedPANId";
    public static final String SAS_PAN_ID = SAS + ".PANId";
    public static final String SAS_CHANNEL_MASK = SAS + ".ChannelMask";
    public static final String SAS_PROTOCOL_VERSION = SAS + ".ProtocolVersion";
    public static final String SAS_STACK_PROFILE = SAS + ".StackProfile";
    public static final String SAS_STARTUP_CONTROL = SAS + ".StartupControl";
    public static final String SAS_TRUST_CENTER_ADDRESS = SAS + ".TrustCenterAddress";
    public static final String SAS_TRUST_CENTER_MASTER_KEY = SAS + ".TrustCenterMasterKey";
    public static final String SAS_NETWORK_KEY = SAS + ".NetworkKey";
    public static final String SAS_USE_INSECURE_JOIN = SAS + ".UseInsecureJoin";
    public static final String SAS_PRECONFIGURED_LINK_KEY = SAS + ".PreconfiguredLinkKey";
    public static final String SAS_NETWORK_KEY_SEQ_NUM = SAS + ".NetworkKeySeqNum";
    public static final String SAS_NETWORK_KEY_TYPE = SAS + ".NetworkKeyType";
    public static final String SAS_NETWORK_MANAGER_ADDRESS = SAS + ".NetworkManagerAddress";
    
    public void updateParameter(Object key, Object val);
    
    public void updateParameters(Dictionary parameters);
    
    public void commission() throws IOException;
}
