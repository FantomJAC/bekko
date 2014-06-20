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
import com.valleycampus.zigbee.io.ByteUtil;
import com.valleycampus.zigbee.io.Frame;
import com.valleycampus.zigbee.io.FrameBuffer;
import com.valleycampus.zigbee.util.Commons;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class StringDataType extends DataType {
    
    public static StringDataType OCTET_STRING = new StringDataType(true, false, ZCLAttribute.DATA_OCTET_STRING);
    public static StringDataType CHARACTER_STRING = new StringDataType(false, false, ZCLAttribute.DATA_CHARACTER_STRING);
    public static StringDataType LONG_OCTET_STRING = new StringDataType(true, true, ZCLAttribute.DATA_LONG_OCTET_STRING);
    public static StringDataType LONG_CHARACTER_STRING = new StringDataType(false, true, ZCLAttribute.DATA_LOMG_CHARACTER_STRING);
    private boolean octet;
    private boolean extended;
    
    public StringDataType(boolean octet, boolean extended, byte dataType) {
        super(dataType, false);
        this.octet = octet;
        this.extended = extended;
    }

    public boolean isValid(Object data, Object minValue, Object maxValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isReportableChange(Object prev, Object current, Object reportableChange) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Frame toPayload(Object data) {
        if (data == null) {
            return createPayload();
        }
        byte[] payload;
        if (data instanceof String) {
            if (octet) {
                String[] s = Commons.split((String) data, ' ');
                payload = new byte[s.length];
                for (int i = 0; i < s.length; i++) {
                    payload[i] = (byte) Integer.parseInt(s[i], 16);
                }
            } else {
                // TODO: Need to specify charset from ComplexDescriptor.
                payload = ((String) data).getBytes();
            }
        } else if (data instanceof byte[]) {
            payload = (byte[]) data;
        } else {
            throw new IllegalArgumentException("Cannot parse.");
        }
        StringFrame frame = new StringFrame(extended);
        frame.data = payload;
        return frame;
    }

    public Frame createPayload() {
        return new StringFrame(extended);
    }

    public Object toData(Frame payload) {
        StringFrame frame = (StringFrame) payload;
        if (frame.data == null) {
            return null;
        }
        
        if (octet) {
            return frame.data;
        } else {
            // TODO: Need to specify charset from ComplexDescriptor.
            return new String(frame.data);
        }
    }
    
    private static class StringFrame implements Frame {

        private byte[] data;
        private boolean extended;
        
        public StringFrame(boolean extended) {
            this.extended = extended;
        }

        public void pull(FrameBuffer frameBuffer) {
            int length = -1;
            if (data != null) {
                length = data.length;
            }
            if (!extended) {
                frameBuffer.putInt8(length);
            } else {
                frameBuffer.putInt16(length);
            }
            if (length > 0) {
                frameBuffer.put(data, 0, length);
            }
        }

        public int quote() {
            int q = (!extended ? ByteUtil.INT_8_SIZE : ByteUtil.INT_16_SIZE);
            if (data != null) {
                q += data.length;
            }
            return q;
        }

        public void drain(FrameBuffer frameBuffer) {
            int length;
            if (!extended) {
                length = frameBuffer.getInt8();
                if (length == 0xFF) {
                    return;
                }
            } else {
                length = frameBuffer.getInt16();
                if (length == 0xFFFF) {
                    return;
                }
            }
            data = frameBuffer.getByteArray(length);
        }
        
    }
}
