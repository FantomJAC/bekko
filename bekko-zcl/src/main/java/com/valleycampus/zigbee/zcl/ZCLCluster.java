/*
 * Copyright (C) 2014 Valley Campus Japan, Inc.
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
package com.valleycampus.zigbee.zcl;

import com.valleycampus.zigbee.ZigBeeAddress;
import com.valleycampus.zigbee.zcl.command.ZCLCommand;

/**
 *
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public interface ZCLCluster {
    
    public static final short ZCL_BASIC_CLUSTER_ID                         = (short) 0x0000;
    public static final short ZCL_POWER_CONFIG_CLUSTER_ID                  = (short) 0x0001;
    public static final short ZCL_DEVICE_TEMP_CLUSTER_ID                   = (short) 0x0002;
    public static final short ZCL_IDENTIFY_CLUSTER_ID                      = (short) 0x0003;
    public static final short ZCL_GROUPS_CLUSTER_ID                        = (short) 0x0004;
    public static final short ZCL_SCENES_CLUSTER_ID                        = (short) 0x0005;
    public static final short ZCL_ON_OFF_CLUSTER_ID                        = (short) 0x0006;
    public static final short ZCL_ON_OFF_SWITCH_CONFIG_CLUSTER_ID          = (short) 0x0007;
    public static final short ZCL_LEVEL_CONTROL_CLUSTER_ID                 = (short) 0x0008;
    public static final short ZCL_ALARM_CLUSTER_ID                         = (short) 0x0009;
    public static final short ZCL_TIME_CLUSTER_ID                          = (short) 0x000A;
    public static final short ZCL_RSSI_LOCATION_CLUSTER_ID                 = (short) 0x000B;
    public static final short ZCL_COMMISSIONING_CLUSTER_ID                 = (short) 0x0015;
    public static final short ZCL_SHADE_CONFIG_CLUSTER_ID                  = (short) 0x0100;
    public static final short ZCL_PUMP_CONFIG_CONTROL_CLUSTER_ID           = (short) 0x0200;
    public static final short ZCL_THERMOSTAT_CLUSTER_ID                    = (short) 0x0201;
    public static final short ZCL_FAN_CONTROL_CLUSTER_ID                   = (short) 0x0202;
    public static final short ZCL_DEHUMID_CONTROL_CLUSTER_ID               = (short) 0x0203;
    public static final short ZCL_THERMOSTAT_UI_CONFIG_CLUSTER_ID          = (short) 0x0204;
    public static final short ZCL_COLOR_CONTROL_CLUSTER_ID                 = (short) 0x0300;
    public static final short ZCL_BALLAST_CONFIGURATION_CLUSTER_ID         = (short) 0x0301;
    public static final short ZCL_ILLUM_MEASUREMENT_CLUSTER_ID             = (short) 0x0400;
    public static final short ZCL_ILLUM_LEVEL_SENSING_CLUSTER_ID           = (short) 0x0401;
    public static final short ZCL_TEMP_MEASUREMENT_CLUSTER_ID              = (short) 0x0402;
    public static final short ZCL_PRESSURE_MEASUREMENT_CLUSTER_ID          = (short) 0x0403;
    public static final short ZCL_FLOW_MEASUREMENT_CLUSTER_ID              = (short) 0x0404;
    public static final short ZCL_RELATIVE_HUMIDITY_MEASUREMENT_CLUSTER_ID = (short) 0x0405;
    public static final short ZCL_OCCUPANCY_SENSING_CLUSTER_ID             = (short) 0x0406;
    public static final short ZCL_IAS_ZONE_CLUSTER_ID                      = (short) 0x0500;
    public static final short ZCL_IAS_ACE_CLUSTER_ID                       = (short) 0x0501;
    public static final short ZCL_IAS_WD_CLUSTER_ID                        = (short) 0x0502;
    public static final short ZCL_DOOR_LOCK_CLUSTER_ID                     = (short) 0x0101;
    public static final short ZCL_WINDOW_COVERING_CLUSTER_ID               = (short) 0x0102;
    public static final short ZCL_GENERIC_TUNNEL_CLUSTER_ID                = (short) 0x0600;
    public static final short ZCL_BACNET_PROTOCOL_TUNNEL_CLUSTER_ID        = (short) 0x0601;
    public static final short ZCL_OTA_BOOTLOAD_CLUSTER_ID                  = (short) 0x0019;
    public static final short ZCL_DEMAND_RESPONSE_LOAD_CONTROL_CLUSTER_ID  = (short) 0x0701;
    public static final short ZCL_SIMPLE_METERING_CLUSTER_ID               = (short) 0x0702;
    public static final short ZCL_PRICE_CLUSTER_ID                         = (short) 0x0700;
    public static final short ZCL_MESSAGING_CLUSTER_ID                     = (short) 0x0703;
    public static final short ZCL_TUNNELING_CLUSTER_ID                     = (short) 0x0704;
    public static final short ZCL_PREPAYMENT_CLUSTER_ID                    = (short) 0x0705;
    public static final short ZCL_KEY_ESTABLISHMENT_CLUSTER_ID             = (short) 0x0800;
    public final static byte STATUS_SUCCESS = 0x00;
    public final static byte STATUS_FAILURE = 0x01;
    public final static byte STATUS_NOT_AUTHORIZED = 0x7E;
    public final static byte STATUS_RESERVED_FIELD_NOT_ZERO = 0x7F;
    public final static byte STATUS_MALFORMED_COMMAND = (byte) 0x80;
    public final static byte STATUS_UNSUP_CLUSTER_COMMAND = (byte) 0x81;
    public final static byte STATUS_UNSUP_GENERAL_COMMAND = (byte) 0x82;
    public final static byte STATUS_UNSUP_MANUF_CLUSTER_COMMAND = (byte) 0x83;
    public final static byte STATUS_UNSUP_MANUF_GENERAL_COMMAND = (byte) 0x84;
    public final static byte STATUS_INVALID_FIELD = (byte) 0x85;
    public final static byte STATUS_UNSUPPORTED_ATTRIBUTE = (byte) 0x86;
    public final static byte STATUS_INVALID_VALUE = (byte) 0x87;
    public final static byte STATUS_READ_ONLY = (byte) 0x88;
    public final static byte STATUS_INSUFFICIENT_SPACE = (byte) 0x89;
    public final static byte STATUS_DUPLICATE_EXISTS = (byte) 0x8A;
    public final static byte STATUS_NOT_FOUND = (byte) 0x8B;
    public final static byte STATUS_UNREPORTABLE_ATTRIBUTE = (byte) 0x8C;
    public final static byte STATUS_INVALID_DATA_TYPE = (byte) 0x8D;
    public final static byte STATUS_INVALID_SELECTOR = (byte) 0x8E;
    public final static byte STATUS_WRITE_ONLY = (byte) 0x8F;
    public final static byte STATUS_INCONSISTENT_STARTUP_STATE = (byte) 0x90;
    public final static byte STATUS_DEFINED_OUT_OF_BAND = (byte) 0x91;
    public final static byte STATUS_HARDWARE_FAILURE = (byte) 0xC0;
    public final static byte STATUS_SOFTWARE_FAILURE = (byte) 0xC1;
    public final static byte STATUS_CALIBRATION_ERROR = (byte) 0xC2;
    public static final byte DIRECTION_INPUT = 0x00;    // Server
    public static final byte DIRECTION_OUTPUT = 0x01;   // Client

    public short getClusterId();
    
    public byte getDirection();
    
    public ZCLCommandPacket doResponse(ZCLCommandPacket request) throws UnsupportedCommandException;
    
    public ZCLAttribute[] getAttributes();

    public ZCLAttribute getAttribute(short attributeId);
    
    public RemoteAttributeCapability getAttributeCapability(short attributeId);
    
    public void generalCommandResponseReceived(ZigBeeAddress address, int endpoint, int tsn, ZCLCommand command);
}
