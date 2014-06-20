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
package com.valleycampus.xbee.api.common;

import com.valleycampus.zigbee.io.Frame;
import com.valleycampus.zigbee.io.FrameBuffer;
import com.valleycampus.zigbee.util.Commons;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class IODataSample implements Frame {

    private int digitalChannelMask;
    private int analogChannelMask;
    private int digitalSample;
    private int[] analogSample;

    public void drain(FrameBuffer frameBuffer) {
        digitalChannelMask = frameBuffer.getInt16();
        analogChannelMask = frameBuffer.getInt8();
        
        if (digitalChannelMask > 0) {
            digitalSample = frameBuffer.getInt16();
        }
        
        if (analogChannelMask > 0) {
            int num = Commons.countBit(analogChannelMask);
            analogSample = new int[num];
            for (int i = 0; i < num; i++) {
                analogSample[i] = frameBuffer.getInt16();
            }
        }
    }
    
    public final void pull(FrameBuffer frameBuffer) {
        throw new UnsupportedOperationException("Drain Only");
    }

    public int quote() {
        throw new UnsupportedOperationException("Not Supported");
    }
    
    public boolean isDigialSampleAvailable(int index) {
        return (digitalChannelMask & (1 << index)) > 0;
    }
    
    public boolean getDigitalSample(int index) {
        return (digitalSample & (1 << index)) > 0;
    }
    
    public boolean isAnalogSampleAvailable(int index) {
        return (analogChannelMask & (1 << index)) > 0;
    }
    
    public int getAnalogSample(int index) {
        int sampleIndex = 0;
        for (int i = 0; i < 8; i++) {
            int sample = -1;
            if (isAnalogSampleAvailable(i)) {
                sample = analogSample[sampleIndex++];
            }
            if (index == i) {
                return sample;
            }
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    /**
     * @return the digitalChannelMask
     */
    public int getDigitalChannelMask() {
        return digitalChannelMask;
    }

    /**
     * @return the analogChannelMask
     */
    public int getAnalogChannelMask() {
        return analogChannelMask;
    }

    /**
     * @return the digitalSample
     */
    public int getDigitalSample() {
        return digitalSample;
    }

    /**
     * @return the analogSample
     */
    public int[] getAnalogSample() {
        return analogSample;
    }
}
