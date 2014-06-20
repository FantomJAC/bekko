/*
 * Copyright (C) 2012 Valley Campus Japan, Inc.
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
package com.valleycampus.xbee.api;

import com.valleycampus.xbee.api.common.ATCommand;
import com.valleycampus.xbee.api.common.ATCommandResponse;
import java.io.IOException;
import com.valleycampus.zigbee.io.ByteUtil;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class XBeeIO {
    
    public static final int DEFAULT_TIMEOUT = 1000;
    
    private XBeeAPI driver;
    private boolean queueing;
    private int timeout;
    
    public XBeeIO(XBeeAPI driver) {
        this.driver = driver;
        setTimeout(DEFAULT_TIMEOUT);
    }
    
    private void checkResponse(ATCommandResponse resp) throws IOException {
        if (resp.getStatus() != ATCommandResponse.STATUS_OK) {
            switch (resp.getStatus()) {
            case ATCommandResponse.STATUS_ERROR:
                throw new IOException("Status Error");
            case ATCommandResponse.STATUS_INVALID_COMMAND:
                throw new IOException("Invalid Command");
            case ATCommandResponse.STATUS_INVALID_PARAMETER:
                throw new IOException("Invalid Parameter");
            default:
                throw new IOException("Unknown Error");
            }
        }
    }
    
    public long read64(String cmd) throws IOException {
        return ByteUtil.BIG_ENDIAN.toInt64(readMulti8(cmd), 0);
    }
    
    public int read32(String cmd) throws IOException {
        return ByteUtil.BIG_ENDIAN.toInt32(readMulti8(cmd), 0);
    }
    
    public int read16(String cmd) throws IOException {
        return ByteUtil.BIG_ENDIAN.toInt16(readMulti8(cmd), 0);
    }
    
    public int read8(String cmd) throws IOException {
        return readMulti8(cmd)[0] & 0xFF;
    }
    
    public byte[] readMulti8(String cmd) throws IOException {
        ATCommandResponse resp = (ATCommandResponse) driver.syncSubmit(new ATCommand(cmd), timeout);
        checkResponse(resp);
        return resp.getData();
    }
    
    public void write64(String cmd, long dat) throws IOException {
        writeMulti8(cmd, ByteUtil.BIG_ENDIAN.toByteArray(dat, ByteUtil.INT_64_SIZE));
    }
    
    public void write32(String cmd, int dat) throws IOException {
        writeMulti8(cmd, ByteUtil.BIG_ENDIAN.toByteArray(dat, ByteUtil.INT_32_SIZE));
    }
    
    public void write16(String cmd, int dat) throws IOException {
        writeMulti8(cmd, ByteUtil.BIG_ENDIAN.toByteArray(dat, ByteUtil.INT_16_SIZE));
    }
    
    public void write8(String cmd, int dat) throws IOException {
        writeMulti8(cmd, new byte[] {(byte) dat});
    }
    
    public void writeMulti8(String cmd, byte[] value) throws IOException {
        ATCommand at = new ATCommand(cmd);
        if (isQueueing()) {
            at.setFrameType(XBeeFrame.API_AT_COMMAND_QPV);
        }
        if (value != null) {
            at.setValue(value);
        }
        ATCommandResponse resp = (ATCommandResponse) driver.syncSubmit(at, timeout);
        checkResponse(resp);
    }

    /**
     * @return the queueing
     */
    public boolean isQueueing() {
        return queueing;
    }

    /**
     * @param queueing the queueing to set
     */
    public void setQueueing(boolean queueing) {
        this.queueing = queueing;
    }
    
    public void applyChanges() throws IOException {
        setQueueing(false);
        driver.asyncSubmit(new ATCommand("AC"), false);
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
        }
    }
    
    public void write() throws IOException {
        driver.asyncSubmit(new ATCommand("WR"), false);
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
        }
    }
    
    public void reset() throws IOException {
        driver.asyncSubmit(new ATCommand("FR"), false);
        try {
            Thread.sleep(2500);
        } catch (InterruptedException ex) {
        }
    }

    /**
     * @return the timeout
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
