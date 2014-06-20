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
import java.util.HashMap;
import com.valleycampus.zigbee.io.Frame;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public abstract class DataType {
    
    private static final HashMap TYPE_MAP = new HashMap();
    private final byte dataType;
    private final boolean analog;
    
    protected DataType(byte dataType, boolean analog) {
        this.dataType = dataType;
        this.analog = analog;
        TYPE_MAP.put(new Byte(dataType), this);
    }
    
    public static DataType getDataType(byte dataType) {
        Object o = TYPE_MAP.get(new Byte(dataType));
        if (o != null) {
            return (DataType) o;
        }
        return null;
    }
    
    public byte getDataType() {
        return dataType;
    }
    
    public boolean isAnalog() {
        return analog;
    }
    
    public abstract boolean isValid(Object data, Object minValue, Object maxValue);
    
    public abstract boolean isReportableChange(Object prev, Object current, Object reportableChange);
    
    public abstract Frame toPayload(Object data);
    
    public abstract Frame createPayload();
    
    public abstract Object toData(Frame payload);
    
    public static boolean isReportableDataType(byte dataType) {
        switch (dataType) {
        case ZCLAttribute.DATA_ARRAY:
        case ZCLAttribute.DATA_STRUCTURE:
        case ZCLAttribute.DATA_SET:
        case ZCLAttribute.DATA_BAG:
            return false;
        }
        return true;
    }
}
