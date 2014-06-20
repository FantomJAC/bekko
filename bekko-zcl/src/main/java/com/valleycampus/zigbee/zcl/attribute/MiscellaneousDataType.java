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

import com.valleycampus.zigbee.IEEEAddress;
import com.valleycampus.zigbee.zcl.ZCLAttribute;
import com.valleycampus.zigbee.zcl.command.SimpleValueField;
import com.valleycampus.zigbee.io.Frame;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public final class MiscellaneousDataType extends DataType {
 
    public static final Object NOT_EQUAL = new Object();
    public static final MiscellaneousDataType MISC_IEEE_ADDRESS = new MiscellaneousDataType(8, ZCLAttribute.DATA_IEEE_ADDR);
    
    private int octets;
    
    private MiscellaneousDataType(int octets, byte dataType) {
        super(dataType, false);
        this.octets = octets;
    }
    
    public boolean isValid(Object data, Object minValue, Object maxValue) {
        return true;
    }

    public boolean isReportableChange(Object prev, Object current, Object reportableChange) {
        if (prev.equals(current)) {
            return false;
        }
        return true;
    }

    public Frame toPayload(Object data) {
        if (data instanceof IEEEAddress) {
            IEEEAddress address = (IEEEAddress) data;
            SimpleValueField svf = new SimpleValueField(octets);
            svf.setValue(address.array());
            return svf;
        }
        
        return null;
    }

    public Frame createPayload() {
        return new SimpleValueField(octets);
    }

    public Object toData(Frame payload) {
        SimpleValueField svf = (SimpleValueField) payload;
        if (getDataType() == ZCLAttribute.DATA_IEEE_ADDR) {
            return IEEEAddress.getByAddress(svf.getValue());
        }
        return null;
    }
}
