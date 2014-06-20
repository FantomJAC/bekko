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
package com.valleycampus.zigbee;

/**
 * NodeDescriptor, part of the ZigBee Application Framework.
 * ZigBee Specification (Document 053474r17) 2.3.2.3
 * 
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public interface ZigBeeNodeDescriptor {

    public static final byte LOGICAL_TYPE_COORDINATOR = 0x0;
    public static final byte LOGICAL_TYPE_ROUTER = 0x1;
    public static final byte LOGICAL_TYPE_END_DEVICE = 0x2;

    public byte getLogicalType();

    public boolean isComplexDescriptorAvailable();

    public boolean isUserDescriptorAvailable();

    public byte getAPSFlags();
    
    public static final byte FREQUENCY_868_TO_868_6 = 0x1 << 0;
    public static final byte FREQUENCY_902_TO_928 = 0x1 << 2;
    public static final byte FREQUENCY_2400_TO_2483_5 = 0x1 << 3;

    public byte getFrequencyBand();
    
    public static final byte MAC_CAP_ALTERNATE_PAN_COORDINATOR = 0x01;
    public static final byte MAC_CAP_DEVICE_TYPE = 0x02;
    public static final byte MAC_CAP_POWER_SOURCE = 0x04;
    public static final byte MAC_CAP_RECEIVER_ON_WHEN_IDLE = 0x08;
    public static final byte MAC_CAP_SECURITY_CAP = 0x40;
    public static final byte MAC_CAP_ALLOCATE_ADDRESS = (byte) 0x80;

    public byte getMACCapabilityFlags();

    public short getManufacturerCode();

    public int getMaximumBufferSize();

    public int getMaximumIncomingTransferSize();
    
    public static final short SERVER_MASK_PRIMARY_TRUST_CENTER = 0x1 << 0;
    public static final short SERVER_MASK_BACKUP_TRUST_CENTER = 0x1 << 1;
    public static final short SERVER_MASK_PRIMARY_BINDING_TABLE_CACHE = 0x1 << 2;
    public static final short SERVER_MASK_BACKUP_BINDING_TABLE_CACHE = 0x1 << 3;
    public static final short SERVER_MASK_PRIMARY_DISCOVERY_CACHE = 0x1 << 4;
    public static final short SERVER_MASK_BACKUP_DISCOVERY_CACHE = 0x1 << 5;
    public static final short SERVER_MASK_NETWORK_MANAGER = 0x1 << 6;

    public short getServerMask();

    public int getMaximumOutgoingTransferSize();
    
    public static final byte DESCRIPTOR_CAP_EXT_ACTIVE_EP_LIST_AVAILABLE = 0x01 << 0;
    public static final byte DESCRIPTOR_CAP_EXT_SIMPLE_EP_LIST_AVAILABLE = 0x01 << 1;

    public byte getDescriptorCapability();
}
