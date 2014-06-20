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
package com.valleycampus.xbee.api.common;

import com.valleycampus.xbee.api.XBeeResponse;
import com.valleycampus.zigbee.io.FrameBuffer;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class ModemStatus extends XBeeResponse {

    public static final int MDMSTS_HW_RESET = 0;
    public static final int MDMSTS_WDT_RESET = 1;
    public static final int MDMSTS_ASSOCIATED = 2;
    public static final int MDMSTS_DISASSOCIATED = 3;
    public static final int MDMSTS_SYNC_LOST = 4;           // Obsolete
    public static final int MDMSTS_COORDINATOR_RALGN = 5;   // Obsolete
    public static final int MDMSTS_COORDINATOR_STARTED = 6;
    public static final int MDMSTS_VSUPPLY_LIMIT_EXCEED = 0x0D;
    public static final int MDMSTS_CONFIG_CHANGED = 0x11;
    public static final int MDMSTS_SE_KEYEST_COMPLETE = 0x10;
    public static final int MDMSTS_STACK_ERROR = 0x80;
    private int status;

    public ModemStatus() {
        
    }
    
    public void drain(FrameBuffer frameBuffer) {
        super.drain(frameBuffer);
        status = frameBuffer.getInt8();
    }

    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }
}
