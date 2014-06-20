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

import java.util.Vector;
import com.valleycampus.zigbee.io.ByteUtil;
import com.valleycampus.zigbee.io.Frame;
import com.valleycampus.zigbee.io.FrameBuffer;

/**
 *
 * @author Eisuke Yasato <eyasato@valleycampus.com>
 */
public class ReadReportingConfiguration implements ZCLCommand {

    private Record[] records;

    public byte getCommandId() {
        return READ_REPORTING_CONFIGURATION;
    }

    public void pull(FrameBuffer frameBuffer) {
        for (int i = 0; i < getRecords().length; i++) {
            getRecords()[i].pull(frameBuffer);
        }
    }

    public int quote() {
        int q = 0;
        for (int i = 0; i < getRecords().length; i++) {
            q += getRecords()[i].quote();
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

        private byte direction;
        private short attributeId;

        public void pull(FrameBuffer fb) {
            fb.putInt8(direction);
            fb.putInt16(attributeId);
        }

        public int quote() {
            int q = 0;
            q += ByteUtil.INT_8_SIZE;
            q += ByteUtil.INT_16_SIZE;

            return q;
        }

        public void drain(FrameBuffer fb) {
            setDirection((byte) fb.getInt8());
            setAttributeId((short) fb.getInt16());
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
    }
}
