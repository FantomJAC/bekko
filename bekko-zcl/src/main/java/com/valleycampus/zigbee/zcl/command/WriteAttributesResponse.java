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
import java.util.ArrayList;
import com.valleycampus.zigbee.io.Frame;
import com.valleycampus.zigbee.io.FrameBuffer;
import com.valleycampus.zigbee.io.ByteUtil;

/**
 *
 * @author Eisuke Yasato <eyasato@valleycampus.com>
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class WriteAttributesResponse implements ZCLCommand {

    private StatusRecord[] records;
    private boolean allSuccess;
    
    public byte getCommandId() {
        return WRITE_ATTRIBUTES_RSP;
    }


    public void pull(FrameBuffer frameBuffer) {
        if (allSuccess) {
            frameBuffer.put(ZCLCluster.STATUS_SUCCESS);
        } else {
            for (int i = 0; i < records.length; i++) {
                records[i].pull(frameBuffer);
            }
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
        if (frameBuffer.getRemaining() == 1) {
            if (frameBuffer.getByte() == ZCLCluster.STATUS_SUCCESS) {
                allSuccess = true;
            } else {
                allSuccess = false;
            }
        } else {
            ArrayList list = new ArrayList();
            while (frameBuffer.getRemaining() > 0) {
                StatusRecord rec = new StatusRecord();
                rec.drain(frameBuffer);
                list.add(rec);
            }
            records = (StatusRecord[]) list.toArray(new StatusRecord[0]);
        }
    }

    /**
     * @return the record
     */
    public StatusRecord[] getRecords() {
        return records;
    }

    /**
     * @param record the record to set
     */
    public void setRecords(StatusRecord[] record) {
        this.records = record;
    }

    /**
     * @return the allSuccess
     */
    public boolean isAllSuccess() {
        return allSuccess;
    }

    /**
     * @param allSuccess the allSuccess to set
     */
    public void setAllSuccess(boolean allSuccess) {
        this.allSuccess = allSuccess;
    }

    public class StatusRecord implements Frame {

        private byte status;
        private short attributeId;

        public void pull(FrameBuffer frameBuffer) {
            frameBuffer.put(status);
            frameBuffer.putInt16(attributeId);
        }

        public int quote() {
            int q = 0;
            q += ByteUtil.INT_8_SIZE;

            return q;
        }

        public void drain(FrameBuffer frameBuffer) {
            status = frameBuffer.getByte();
            attributeId = frameBuffer.getShort();
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
