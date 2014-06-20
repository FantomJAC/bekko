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

import java.io.IOException;

/**
 * Local attribute storage interface.
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public interface ZCLAttribute {

    public final static byte DATA_NULL = 0x00;
    public final static byte DATA_8BIT = 0x08;
    public final static byte DATA_16BIT = 0x09;
    public final static byte DATA_24BIT = 0x0A;
    public final static byte DATA_32BIT = 0x0B;
    public final static byte DATA_40BIT = 0x0C;
    public final static byte DATA_48BIT = 0x0D;
    public final static byte DATA_56BIT = 0x0E;
    public final static byte DATA_64BIT = 0x0F;
    public final static byte DATA_BOOLEAN = 0x10;
    public final static byte DATA_8BIT_BITMAP = 0x18;
    public final static byte DATA_16BIT_BITMAP = 0x19;
    public final static byte DATA_24BIT_BITMAP = 0x1A;
    public final static byte DATA_32BIT_BITMAP = 0x1B;
    public final static byte DATA_40BIT_BITMAP = 0x1C;
    public final static byte DATA_48BIT_BITMAP = 0x1D;
    public final static byte DATA_56BIT_BITMAP = 0x1E;
    public final static byte DATA_64BIT_BITMAP = 0x1F;
    public final static byte DATA_UNSIGNED_8BIT_INT = 0x20;
    public final static byte DATA_UNSIGNED_16BIT_INT = 0x21;
    public final static byte DATA_UNSIGNED_24BIT_INT = 0x22;
    public final static byte DATA_UNSIGNED_32BIT_INT = 0x23;
    public final static byte DATA_UNSIGNED_40BIT_INT = 0x24;
    public final static byte DATA_UNSIGNED_48BIT_INT = 0x25;
    public final static byte DATA_UNSIGNED_56BIT_INT = 0x26;
    public final static byte DATA_UNSIGNED_64BIT_INT = 0x27;
    public final static byte DATA_SIGNED_8BIT_INT = 0x28;
    public final static byte DATA_SIGNED_16BIT_INT = 0x29;
    public final static byte DATA_SIGNED_24BIT_INT = 0x2A;
    public final static byte DATA_SIGNED_32BIT_INT = 0x2B;
    public final static byte DATA_SIGNED_40BIT_INT = 0x2C;
    public final static byte DATA_SIGNED_48BIT_INT = 0x2D;
    public final static byte DATA_SIGNED_56BIT_INT = 0x2E;
    public final static byte DATA_SIGNED_64BIT_INT = 0x2F;
    public final static byte DATA_8BIT_ENUMERATION = 0x30;
    public final static byte DATA_16BIT_ENUMERATION = 0x31;
    public final static byte DATA_SEMI_PRECISION = 0x38;
    public final static byte DATA_SINGLE_PRECISION = 0x39;
    public final static byte DATA_DOUBLE_PRECISION = 0x3A;
    public final static byte DATA_OCTET_STRING = 0x41;
    public final static byte DATA_CHARACTER_STRING = 0x42;
    public final static byte DATA_LONG_OCTET_STRING = 0x43;
    public final static byte DATA_LOMG_CHARACTER_STRING = 0x44;
    public final static byte DATA_ARRAY = 0x48;
    public final static byte DATA_STRUCTURE = 0x4C;
    public final static byte DATA_SET = 0x50;
    public final static byte DATA_BAG = 0x51;
    public final static byte DATA_TIME_OF_DAY = (byte) 0xE0;
    public final static byte DATA_DATE = (byte) 0xE1;
    public final static byte DATA_UTC_TIME = (byte) 0xE2;
    public final static byte DATA_CLUSTER_ID = (byte) 0xE8;
    public final static byte DATA_ATTRIBUTE_ID = (byte) 0xE9;
    public final static byte DATA_BACNET_OID = (byte) 0xEA;
    public final static byte DATA_IEEE_ADDR = (byte) 0xF0;
    public final static byte DATA_128BIT_SECURITY_KEY = (byte) 0xF1;
    public final static byte DATA_UNKNOWN = (byte) 0xFF;
    
    public short getAttributeId();
    
    public boolean isReadable();
    
    public boolean isWritable();
    
    public boolean isReportable();
    
    public int getMinimumReportingInterval();
    
    public byte getDataType();
    
    public Object getData() throws IOException;
    
    public boolean setData(Object data) throws IOException;
    
    public void resetData() throws IOException;
}
