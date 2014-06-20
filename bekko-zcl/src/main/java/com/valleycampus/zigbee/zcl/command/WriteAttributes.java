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
package com.valleycampus.zigbee.zcl.command;

import com.valleycampus.zigbee.zcl.attribute.DataType;
import java.util.Vector;
import com.valleycampus.zigbee.io.Frame;
import com.valleycampus.zigbee.io.FrameBuffer;
import com.valleycampus.zigbee.io.ByteUtil;

/**
 *
 * @author Eisuke Yasato <eyasato@valleycampus.com>
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class WriteAttributes implements ZCLCommand {

    private Record[] records;
    private final byte commandId;
    
    public WriteAttributes(byte commandId) {
        if (commandId != WRITE_ATTRIBUTES &&
            commandId != WRITE_ATTRIBUTES_NO_RSP &&
            commandId != WRITE_ATTRIBUTES_UNDIVIDED) {
            throw new IllegalArgumentException();
        }
        this.commandId = commandId;
    }
    
    public byte getCommandId() {
        return commandId;
    }

    public void pull(FrameBuffer frameBuffer) {
        for (int i = 0; i < records.length; i++) {
            records[i].pull(frameBuffer);
        }
    }

    public int quote() {
        int q = 0;
        for (int i = 0; i < records.length; i++) {
            q += records[i].quote();
        }
        return q;
    }

    public void drain(FrameBuffer frameBuffer) {
        Vector v = new Vector();
        while (frameBuffer.getRemaining() > 0) {
            Record rec = new Record();
            rec.drain(frameBuffer);
            v.add(rec);
        }
        records = (Record[]) v.toArray(new Record[0]);
    }

    /**
     * @return the records
     */
    public Record[] getRecords() {
        return records;
    }

    /**
     * @param records the records to set
     */
    public void setRecords(Record[] records) {
        this.records = records;
    }

    public static class Record implements Frame {

        private short attributeId;
        private byte attributeDataType;
        private Object attributeData;

        public void pull(FrameBuffer frameBuffer) {
            frameBuffer.putInt16(attributeId);
            frameBuffer.putInt8(attributeDataType);
            DataType dataType = DataType.getDataType(attributeDataType);
            if (dataType != null) {
                dataType.toPayload(attributeData).pull(frameBuffer);
            }
        }

        public int quote() {
            int q = 0;
            q += ByteUtil.INT_16_SIZE;
            q += ByteUtil.INT_8_SIZE;
            if (attributeData != null) {
                DataType dataType = DataType.getDataType(attributeDataType);
                if (dataType != null) {
                    q += dataType.toPayload(attributeData).quote();
                }
            }
            return q;
        }

        public void drain(FrameBuffer frameBuffer) {
            attributeId = (short) frameBuffer.getInt16();
            attributeDataType = (byte) frameBuffer.getInt8();
            DataType dataType = DataType.getDataType(attributeDataType);
            if (dataType != null) {
                Frame payload = dataType.createPayload();
                payload.drain(frameBuffer);
                attributeData = dataType.toData(payload);
            }
        }

        /**
         * @return the attributeId
         */
        public short getAttributeId() {
            return attributeId;
        }

        /**
         * @param attributeId the attributeId to set
         */
        public void setAttributeId(short attributeId) {
            this.attributeId = attributeId;
        }

        /**
         * @return the attributeDataType
         */
        public byte getDataType() {
            return attributeDataType;
        }

        /**
         * @param attributeDataType the attributeDataType to set
         */
        public void setDataType(byte attributeDataType) {
            this.attributeDataType = attributeDataType;
        }

        /**
         * @return the data
         */
        public Object getData() {
            return attributeData;
        }

        /**
         * @param data the data to set
         */
        public void setData(Object data) {
            this.attributeData = data;
        }
    }
}
