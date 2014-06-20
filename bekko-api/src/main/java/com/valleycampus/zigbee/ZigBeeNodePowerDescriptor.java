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
 * NodePowerDescriptor, part of the ZigBee Application Framework.
 * ZigBee Specification (Document 053474r17) 2.3.2.4
 * 
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public interface ZigBeeNodePowerDescriptor {
    
    public byte getCurrentPowerMode();
    
    public static final byte SOURCE_MAINS_POWER = 0x01 << 1;
    public static final byte SOURCE_RECHARGEABLE_BATTERY = 0x01 << 2;
    public static final byte SOURCE_DISPOSABLE_BATTERY = 0x01 << 3;
    
    public byte getAvailablePowerSources();
    
    public byte getCurrentPowerSource();
    
    public static final byte LEVEL_CRITICAL = 0x00;
    public static final byte LEVEL_33_PERCENT = 0x10;
    public static final byte LEVEL_66_PERCENT = 0x20;
    public static final byte LEVEL_100_PERCENT = 0x30;
    
    public byte getCurrentPowerSourceLevel();
}
