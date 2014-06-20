/*
 * Copyright (C) 2013 Valley Campus Japan, Inc.
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
package com.valleycampus.xbee.api.s6;

import com.valleycampus.xbee.api.XBeeResponse;
import com.valleycampus.xbee.api.common.IODataSample;
import com.valleycampus.zigbee.io.FrameBuffer;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class IODataSampleRXIndicator extends XBeeResponse {
    
    private long address64;
    private byte rssi;
    private byte receiveOptions;
    private IODataSample[] samples;
    
    public void drain(FrameBuffer frameBuffer) {
        super.drain(frameBuffer);
        address64 = frameBuffer.getInt64();
        rssi = frameBuffer.getByte();
        receiveOptions = frameBuffer.getByte();
        int numSamples = frameBuffer.getInt8();
        samples = new IODataSample[numSamples];
        for (int i = 0; i < numSamples; i++) {
            samples[i] = new IODataSample();
            getSamples()[i].drain(frameBuffer);
        }
    }

    /**
     * @return the address64
     */
    public long getAddress64() {
        return address64;
    }

    /**
     * @return the rssi
     */
    public byte getRSSI() {
        return rssi;
    }

    /**
     * @return the receiveOptions
     */
    public byte getReceiveOptions() {
        return receiveOptions;
    }

    /**
     * @return the samples
     */
    public IODataSample[] getSamples() {
        return samples;
    }
    
}
