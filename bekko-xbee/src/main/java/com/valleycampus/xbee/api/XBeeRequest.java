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

import com.valleycampus.zigbee.io.FrameBuffer;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class XBeeRequest extends XBeeFrame {

    public static final int DEFAULT_FRAME_ID = 1;
    public static final int DEFAULT_RETRY = 3;
    public static final int NO_RESPONSE_FRAME_ID = 0;

    private int frameId;
    private int retry;

    public XBeeRequest() {
        setRetry(DEFAULT_RETRY);
    }
    
    public void pull(FrameBuffer frameBuffer) {
        super.pull(frameBuffer);
        frameBuffer.putInt8(frameId);
    }

    public int quote() {
        return super.quote() + 1;
    }

    public final void drain(FrameBuffer frameBuffer) {
        throw new UnsupportedOperationException("Pull Only");
    }
    

    /**
     * @return the frameId
     */
    public int getFrameId() {
        return frameId;
    }

    /**
     * @param frameId the frameId to set
     */
    public void setFrameId(int frameId) {
        this.frameId = frameId;
    }

    /**
     * @return the retry
     */
    public int getRetry() {
        return retry;
    }

    /**
     * @param retry the retry to set
     */
    public void setRetry(int retry) {
        this.retry = retry;
    }

    public int decrementRetry() {
        return retry--;
    }
}
