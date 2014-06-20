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
public class ReportAttributes implements ZCLCommand {

    private AttributeReport[] reports;
    
    public byte getCommandId() {
        return REPORT_ATTRIBUTES;
    }


    public void pull(FrameBuffer frameBuffer) {
        frameBuffer.setByteOrder(FrameBuffer.BO_LITTLE_ENDIAN);
        for (int i = 0; i < reports.length; i++) {
            reports[i].pull(frameBuffer);
        }
    }

    public int quote() {
        int q = 0;
        for (int i = 0; i < reports.length; i++) {
            q += reports[i].quote();
        }
        return q;
    }

    public void drain(FrameBuffer frameBuffer) {
        frameBuffer.setByteOrder(FrameBuffer.BO_LITTLE_ENDIAN);
        Vector v = new Vector();
        while (frameBuffer.getRemaining() > 0) {
            AttributeReport rec = new AttributeReport();
            rec.drain(frameBuffer);
            v.add(rec);
        }
        setReports((AttributeReport[]) v.toArray(new AttributeReport[0]));
    }

    /**
     * @return the records
     */
    public AttributeReport[] getReports() {
        return reports;
    }

    /**
     * @param reports the reports to set
     */
    public void setReports(AttributeReport[] reports) {
        this.reports = reports;
    }

    public static class AttributeReport implements Frame {

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
            frameBuffer.setByteOrder(FrameBuffer.BO_LITTLE_ENDIAN);
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
         * @return the attributeData
         */
        public Object getData() {
            return attributeData;
        }

        /**
         * @param attributeData the attributeData to set
         */
        public void setData(Object attributeData) {
            this.attributeData = attributeData;
        }
    }
}
