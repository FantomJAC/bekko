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
package com.valleycampus.zigbee.zcl.core;

import com.valleycampus.zigbee.zcl.attribute.DataType;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class ReportingRegistration {

    private short attributeId;
    private int minimumReportingInterval;
    private int maximumReportingInterval;
    private Object reportableChange;
    private ControlBlock controlBlock;
    
    protected ReportingRegistration(short attributeId,
            int minimumReportingInterval, int maximumReportingInterval,
            Object reportableChange) {
        this.attributeId = attributeId;
        this.minimumReportingInterval = minimumReportingInterval;
        this.maximumReportingInterval = maximumReportingInterval;
        this.reportableChange = reportableChange;
        this.controlBlock = new ControlBlock();
    }

    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.attributeId;
        return hash;
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof ReportingRegistration) {
            ReportingRegistration e = (ReportingRegistration) obj;
            return (e.attributeId == this.attributeId);
        }
        return false;
    }

    /**
     * @return the attributeId
     */
    public short getAttributeId() {
        return attributeId;
    }

    /**
     * @return the minimumReportingInterval
     */
    public int getMinimumReportingInterval() {
        return minimumReportingInterval;
    }

    /**
     * @return the maximumReportingInterval
     */
    public int getMaximumReportingInterval() {
        return maximumReportingInterval;
    }

    /**
     * @return the reportableChange
     */
    public Object getReportableChange() {
        return reportableChange;
    }
    
    protected int getCurrentElapsedTime() {
        return controlBlock.elapsedTimeInSecond();
    }
    
    protected boolean isUpdatedInCurrentInterval() {
        return controlBlock.updated;
    }
    
    protected boolean updateValue(DataType dataType, Object data) {
        if (dataType.isReportableChange(controlBlock.prevData, data, reportableChange)) {
            controlBlock.prevData = data;
            controlBlock.updated = true;
            return true;
        }
        return false;
    }
    
    protected void forceUpdateValue(Object data) {
        controlBlock.prevData = data;
    }
    
    protected void initCurrentInterval() {
        if (maximumReportingInterval > 0) {
            long gap = controlBlock.elapsedTime() - (maximumReportingInterval * 1000L);
            if (gap < 0) {
                ZCLContext.warn("Gap is less than 0!!");
                gap = 0;
            }
            controlBlock.timestamp = System.currentTimeMillis() - gap;
        } else {
            controlBlock.timestamp = System.currentTimeMillis();
        }
        controlBlock.updated = false;
    }
    
    private class ControlBlock {
        
        private boolean updated;
        private long timestamp;
        private Object prevData;
        
        public ControlBlock() {
            updated = false;
            timestamp = System.currentTimeMillis();
        }
        
        public long elapsedTime() {
            return System.currentTimeMillis() - timestamp;
        }
        
        public int elapsedTimeInSecond() {
            long elapsedTime = System.currentTimeMillis() - timestamp;
            return (int) (elapsedTime / 1000L);
        }
    }
}
