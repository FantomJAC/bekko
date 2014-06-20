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
 * SimpleDescriptor, part of the ZigBee Application Framework.
 * ZigBee Specification (Document 053474r17) 2.3.2.5
 * 
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public interface ZigBeeSimpleDescriptor {
    
    public int getEndpoint();
    
    public short getApplicationProfileIdentifier();
    
    public short getApplicationDeviceIdentifier();
    
    public byte getApplicationDeviceVersion();
    
    public int getApplicationInputClusterCount();
    
    public short[] getApplicationInputClusterList();
    
    public int getApplicationOutputClusterCount();
    
    public short[] getApplicationOutputClusterList();
}
