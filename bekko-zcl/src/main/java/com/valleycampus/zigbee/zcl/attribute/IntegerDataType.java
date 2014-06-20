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
package com.valleycampus.zigbee.zcl.attribute;

import com.valleycampus.zigbee.zcl.ZCLAttribute;
import com.valleycampus.zigbee.zcl.command.SimpleValueField;
import java.util.BitSet;
import com.valleycampus.zigbee.io.ByteUtil;
import com.valleycampus.zigbee.io.Frame;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public final class IntegerDataType extends DataType {
    
    public static final IntegerDataType GENERAL_DATA_8BIT = new IntegerDataType(1, true, ZCLAttribute.DATA_8BIT);
    public static final IntegerDataType GENERAL_DATA_16BIT = new IntegerDataType(2, true, ZCLAttribute.DATA_16BIT);
    public static final IntegerDataType GENERAL_DATA_24BIT = new IntegerDataType(3, true, ZCLAttribute.DATA_24BIT);
    public static final IntegerDataType GENERAL_DATA_32BIT = new IntegerDataType(4, true, ZCLAttribute.DATA_32BIT);
    public static final IntegerDataType GENERAL_DATA_40BIT = new IntegerDataType(5, true, ZCLAttribute.DATA_40BIT);
    public static final IntegerDataType GENERAL_DATA_48BIT = new IntegerDataType(6, true, ZCLAttribute.DATA_48BIT);
    public static final IntegerDataType GENERAL_DATA_56BIT = new IntegerDataType(7, true, ZCLAttribute.DATA_56BIT);
    public static final IntegerDataType GENERAL_DATA_64BIT = new IntegerDataType(8, true, ZCLAttribute.DATA_64BIT);
    public static final IntegerDataType BITMAP_8BIT = new IntegerDataType(1, true, ZCLAttribute.DATA_8BIT_BITMAP);
    public static final IntegerDataType BITMAP_16BIT = new IntegerDataType(2, true, ZCLAttribute.DATA_16BIT_BITMAP);
    public static final IntegerDataType BITMAP_24BIT = new IntegerDataType(3, true, ZCLAttribute.DATA_24BIT_BITMAP);
    public static final IntegerDataType BITMAP_32BIT = new IntegerDataType(4, true, ZCLAttribute.DATA_32BIT_BITMAP);
    public static final IntegerDataType BITMAP_40BIT = new IntegerDataType(5, true, ZCLAttribute.DATA_40BIT_BITMAP);
    public static final IntegerDataType BITMAP_48BIT = new IntegerDataType(6, true, ZCLAttribute.DATA_48BIT_BITMAP);
    public static final IntegerDataType BITMAP_56BIT = new IntegerDataType(7, true, ZCLAttribute.DATA_56BIT_BITMAP);
    public static final IntegerDataType BITMAP_64BIT = new IntegerDataType(8, true, ZCLAttribute.DATA_64BIT_BITMAP);
    public static final IntegerDataType UNSIGNED_INTEGER_8BIT = new IntegerDataType(1, true, ZCLAttribute.DATA_UNSIGNED_8BIT_INT, true);
    public static final IntegerDataType UNSIGNED_INTEGER_16BIT = new IntegerDataType(2, true, ZCLAttribute.DATA_UNSIGNED_16BIT_INT, true);
    public static final IntegerDataType UNSIGNED_INTEGER_24BIT = new IntegerDataType(3, true, ZCLAttribute.DATA_UNSIGNED_24BIT_INT, true);
    public static final IntegerDataType UNSIGNED_INTEGER_32BIT = new IntegerDataType(4, true, ZCLAttribute.DATA_UNSIGNED_32BIT_INT, true);
    public static final IntegerDataType UNSIGNED_INTEGER_40BIT = new IntegerDataType(5, true, ZCLAttribute.DATA_UNSIGNED_40BIT_INT, true);
    public static final IntegerDataType UNSIGNED_INTEGER_48BIT = new IntegerDataType(6, true, ZCLAttribute.DATA_UNSIGNED_48BIT_INT, true);
    public static final IntegerDataType UNSIGNED_INTEGER_56BIT = new IntegerDataType(7, true, ZCLAttribute.DATA_UNSIGNED_56BIT_INT, true);
    public static final IntegerDataType UNSIGNED_INTEGER_64BIT = new IntegerDataType(8, true, ZCLAttribute.DATA_UNSIGNED_64BIT_INT, true);
    public static final IntegerDataType SIGNED_INTEGER_8BIT = new IntegerDataType(1, false, ZCLAttribute.DATA_SIGNED_8BIT_INT, true);
    public static final IntegerDataType SIGNED_INTEGER_16BIT = new IntegerDataType(2, false, ZCLAttribute.DATA_SIGNED_16BIT_INT, true);
    public static final IntegerDataType SIGNED_INTEGER_24BIT = new IntegerDataType(3, false, ZCLAttribute.DATA_SIGNED_24BIT_INT, true);
    public static final IntegerDataType SIGNED_INTEGER_32BIT = new IntegerDataType(4, false, ZCLAttribute.DATA_SIGNED_32BIT_INT, true);
    public static final IntegerDataType SIGNED_INTEGER_40BIT = new IntegerDataType(5, false, ZCLAttribute.DATA_SIGNED_40BIT_INT, true);
    public static final IntegerDataType SIGNED_INTEGER_48BIT = new IntegerDataType(6, false, ZCLAttribute.DATA_SIGNED_48BIT_INT, true);
    public static final IntegerDataType SIGNED_INTEGER_56BIT = new IntegerDataType(7, false, ZCLAttribute.DATA_SIGNED_56BIT_INT, true);
    public static final IntegerDataType SIGNED_INTEGER_64BIT = new IntegerDataType(8, false, ZCLAttribute.DATA_SIGNED_64BIT_INT, true);
    public static final IntegerDataType ENUMERATION_8BIT = new IntegerDataType(1, true, ZCLAttribute.DATA_8BIT_ENUMERATION);
    public static final IntegerDataType ENUMERATION_16BIT = new IntegerDataType(2, true, ZCLAttribute.DATA_16BIT_ENUMERATION);
    public static final IntegerDataType IDENTIFIER_CLUSTER_ID = new IntegerDataType(2, true, ZCLAttribute.DATA_CLUSTER_ID);
    public static final IntegerDataType IDENTIFIER_ATTRIBUTE_ID = new IntegerDataType(2, true, ZCLAttribute.DATA_ATTRIBUTE_ID);
    public static final IntegerDataType IDENTIFIER_BACNET_OID = new IntegerDataType(4, true, ZCLAttribute.DATA_BACNET_OID);
    
    private int octets;
    private boolean unsigned;
    
    private IntegerDataType(int octets, boolean unsigned, byte dataType) {
        super(dataType, false);
        this.octets = octets;
        this.unsigned = unsigned;
    }
    
    private IntegerDataType(int octets, boolean unsigned, byte dataType, boolean analog) {
        super(dataType, analog);
        this.octets = octets;
        this.unsigned = unsigned;
    }
    
    private long generateMask() {
        long mask = 0xFF;
        for (int i = 0; i < (octets - 1); i++) {
            mask <<= 8;
            mask |= 0xFF;
        }
        return mask;
    }
    
    public boolean isValid(Object data, Object minValue, Object maxValue) {
        long l = toLong(data);
        long min = toLong(minValue);
        long max = toLong(maxValue);
        return min <= l && l <= max;
    }
    
    public boolean isReportableChange(Object prev, Object current, Object reportableChange) {
        long prevVal = toLong(prev);
        long currentVal = toLong(current);
        long changeVal = prevVal - currentVal;
        if (changeVal == 0) {
            return false;
        } else if (isAnalog()) {
            long rcVal = toLong(reportableChange);
            return Math.abs(changeVal) <= rcVal;
        } else {
            return true;
        }
    }
    
    public Object compare(Object a, Object b) {
        long l1 = toLong(a);
        long l2 = toLong(b);
        long c = l1 - l2;
        return (c != 0) ? new Long(c) : null;
    }
    
    public long toLong(Object data) {
        long value;
        if (data instanceof Number) {
            Number number = (Number) data;
            value = number.longValue();
            if (unsigned) {
                value &= generateMask();
            }
        } else if (data instanceof String) {
            String string = (String) data;
            if (string.startsWith("0x")) {
                value = Long.parseLong(string.substring(2), 16);
                if (!unsigned) {
                    value |= ~generateMask();
                }
            } else {
                value = Long.parseLong(string);
                if (value < 0) {
                    // Reject
                    throw new IllegalArgumentException("Value is less than 0.");
                }
            }
        } else if (data instanceof BitSet) {
            // TODO
            throw new UnsupportedOperationException();
        } else {
            // Reject
            throw new IllegalArgumentException("Cannot parse to Long");
        }
        return value;
    }

    public Frame toPayload(Object data) {
        SimpleValueField svf = new SimpleValueField(octets);
        svf.setValue(ByteUtil.LITTLE_ENDIAN.toByteArray(toLong(data), octets));
        return svf;
    }

    public Frame createPayload() {
        return new SimpleValueField(octets);
    }

    public Object toData(Frame payload) {
        SimpleValueField svf = (SimpleValueField) payload;
        long value = ByteUtil.LITTLE_ENDIAN.toInt(svf.getValue(), 0, octets);
        if (octets == ByteUtil.INT_64_SIZE) {
            return new Long(value);
        }
        if (unsigned) {
            value &= generateMask();
        } else {
            long signedMask = 0x80 << (ByteUtil.BYTE_SIZE * (octets - 1));
            if ((signedMask & value) > 0) {
                value |= ~generateMask();
            }
        }
        return new Long(value);
    }
}
