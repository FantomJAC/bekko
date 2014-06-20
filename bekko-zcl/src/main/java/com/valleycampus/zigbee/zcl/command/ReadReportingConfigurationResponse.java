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
import com.valleycampus.zigbee.io.ByteUtil;
import com.valleycampus.zigbee.io.Frame;
import com.valleycampus.zigbee.io.FrameBuffer;

/**
 *
 * @author Eisuke Yasato <eyasato@valleycampus.com>
 */
public class ReadReportingConfigurationResponse implements ZCLCommand {
    
    private Record[] records;
    
    public byte getCommandId() {
        return READ_REPORTING_CONFIGURATION_RSP;
    }

    public void pull(FrameBuffer fb) {
        for (int i = 0; i < getRecords().length; i++) {
            getRecords()[i].pull(fb);
        }
    }

    public int quote() {
        int q = 0;
        for (int i = 0; i < getRecords().length; i++) {
            q += getRecords()[i].quote();
        }
        return q;
    }

    public void drain(FrameBuffer fb) {
        Vector v = new Vector();
        while (fb.getRemaining() > 0) {
            Record rec = new Record();
            rec.drain(fb);
            v.add(rec);
        }
        setRecords((Record[]) v.toArray(new Record[0]));
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

        private byte status;
        private byte direction;
        private short attributeId;
        private byte attributeDataType;
        private int minReportingInterval;
        private int maxReportingInterval;
        private Object reportableChange;
        private int timeoutPeriod;

        public void pull(FrameBuffer fb) {
            fb.putInt8(status);
            fb.putInt8(direction);
            fb.putInt16(attributeId);
            if (status != ZCLCluster.STATUS_SUCCESS) {
                return;
            }
            if (direction == ZCLCluster.DIRECTION_INPUT) {
                fb.putInt8(attributeDataType);
                fb.putInt16(minReportingInterval);
                fb.putInt16(maxReportingInterval);
                DataType dataType = DataType.getDataType(attributeDataType);
                if (dataType != null && dataType.isAnalog()) {
                    dataType.toPayload(reportableChange).pull(fb);
                }
            } else if (direction == ZCLCluster.DIRECTION_OUTPUT) {
                fb.putInt16(timeoutPeriod);
            }
        }

        public int quote() {
            int q = 0;
            q += ByteUtil.INT_8_SIZE;
            q += ByteUtil.INT_8_SIZE;
            q += ByteUtil.INT_16_SIZE;
            if (status != ZCLCluster.STATUS_SUCCESS) {
                return q;
            }
            if (direction == ZCLCluster.DIRECTION_INPUT) {
                q += ByteUtil.INT_8_SIZE;
                q += ByteUtil.INT_16_SIZE;
                q += ByteUtil.INT_16_SIZE;
                DataType dataType = DataType.getDataType(attributeDataType);
                if (dataType != null && dataType.isAnalog()) {
                    q += dataType.toPayload(reportableChange).quote();
                }
            } else if (direction == ZCLCluster.DIRECTION_OUTPUT) {
                q += ByteUtil.INT_16_SIZE;
            }
            return q;
        }

        public void drain(FrameBuffer fb) {
            status = (byte) fb.getInt8();
            direction = (byte) fb.getInt8();
            attributeId = (short) fb.getInt16();
            if (status != ZCLCluster.STATUS_SUCCESS) {
                return;
            }
            if (direction == ZCLCluster.DIRECTION_INPUT) {
                attributeDataType = (byte) fb.getInt8();
                minReportingInterval = fb.getInt16();
                maxReportingInterval = fb.getInt16();
                DataType dataType = DataType.getDataType(attributeDataType);
                if (dataType != null && dataType.isAnalog()) {
                    Frame payload = dataType.createPayload();
                    payload.drain(fb);
                    reportableChange = dataType.toData(payload);
                }
            } else if (direction == ZCLCluster.DIRECTION_OUTPUT) {
                timeoutPeriod = fb.getInt16();
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
        public byte getAttributeDataType() {
            return attributeDataType;
        }

        /**
         * @param attributeDataType the attributeDataType to set
         */
        public void setAttributeDataType(byte attributeDataType) {
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

        /**
         * @return the status
         */
        public byte getStatus() {
            return status;
        }

        /**
         * @param status the status to set
         */
        public void setStatus(byte status) {
            this.status = status;
        }
    }
}
