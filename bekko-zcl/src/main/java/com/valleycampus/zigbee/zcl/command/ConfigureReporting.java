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

import com.valleycampus.zigbee.zcl.ZCLCluster;
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
public class ConfigureReporting implements ZCLCommand {

    private Record[] records;
    
    public byte getCommandId() {
        return CONFIGURE_REPORTING;
    }

    public void pull(FrameBuffer frameBuffer) {
        for (int i = 0; i < getRecords().length; i++) {
            getRecords()[i].pull(frameBuffer);
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

        private byte direction;
        private short attributeId;
        private byte attributeDataType;
        private int minReportingInterval;
        private int maxReportingInterval;
        private Object reportableChange;
        private int timeoutPeriod;

        public void pull(FrameBuffer frameBuffer) {
            frameBuffer.putInt8(direction);
            frameBuffer.putInt16(attributeId);
            if (direction == ZCLCluster.DIRECTION_INPUT) {
                frameBuffer.putInt8(attributeDataType);
                frameBuffer.putInt16(minReportingInterval);
                frameBuffer.putInt16(maxReportingInterval);
                DataType dataType = DataType.getDataType(attributeDataType);
                if (dataType != null && dataType.isAnalog()) {
                    dataType.toPayload(reportableChange).pull(frameBuffer);
                }
            } else if (direction == ZCLCluster.DIRECTION_OUTPUT) {
                frameBuffer.putInt16(timeoutPeriod);
            }
        }

        public int quote() {
            int q = 0;
            q += ByteUtil.INT_8_SIZE;
            q += ByteUtil.INT_16_SIZE;
            if (direction == ZCLCluster.DIRECTION_INPUT) {
                q += ByteUtil.INT_8_SIZE;
                q += ByteUtil.INT_16_SIZE;
                q += ByteUtil.INT_16_SIZE;
                DataType dataType = DataType.getDataType(attributeDataType);
                if (dataType != null && dataType.isAnalog()) {
                    q += dataType.toPayload(reportableChange).quote();
                }
                return q;
            } else if (direction == ZCLCluster.DIRECTION_OUTPUT) {
                q += ByteUtil.INT_16_SIZE;
                return q;
            }
            return q;
        }

        public void drain(FrameBuffer frameBuffer) {
            direction = (byte) frameBuffer.getInt8();
            attributeId = (short) frameBuffer.getInt16();
            if (direction == ZCLCluster.DIRECTION_INPUT) {
                attributeDataType = (byte) frameBuffer.getInt8();
                minReportingInterval = frameBuffer.getInt16();
                maxReportingInterval = frameBuffer.getInt16();
                DataType dataType = DataType.getDataType(attributeDataType);
                if (dataType != null && dataType.isAnalog()) {
                    Frame payload = dataType.createPayload();
                    payload.drain(frameBuffer);
                    reportableChange = dataType.toData(payload);
                }
            } else if (direction == ZCLCluster.DIRECTION_OUTPUT) {
                timeoutPeriod = frameBuffer.getInt16();
            }
        }

        /**
         * @return the direction
         */
        public byte getDirection() {
            return direction;
        }

        /**
         * @param direction the direction to set
         */
        public void setDirection(byte direction) {
            this.direction = direction;
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
         * @return the minReportingInterval
         */
        public int getMinReportingInterval() {
            return minReportingInterval;
        }

        /**
         * @param minReportingInterval the minReportingInterval to set
         */
        public void setMinReportingInterval(int minReportingInterval) {
            this.minReportingInterval = minReportingInterval;
        }

        /**
         * @return the maxReportingInterval
         */
        public int getMaxReportingInterval() {
            return maxReportingInterval;
        }

        /**
         * @param maxReportingInterval the maxReportingInterval to set
         */
        public void setMaxReportingInterval(int maxReportingInterval) {
            this.maxReportingInterval = maxReportingInterval;
        }

        /**
         * @return the reportableChange
         */
        public Object getReportableChange() {
            return reportableChange;
        }

        /**
         * @param reportableChange the reportableChange to set
         */
        public void setReportableChange(Object reportableChange) {
            this.reportableChange = reportableChange;
        }

        /**
         * @return the timeoutPeriod
         */
        public int getTimeoutPeriod() {
            return timeoutPeriod;
        }

        /**
         * @param timeoutPeriod the timeoutPeriod to set
         */
        public void setTimeoutPeriod(int timeoutPeriod) {
            this.timeoutPeriod = timeoutPeriod;
        }

    }
}
