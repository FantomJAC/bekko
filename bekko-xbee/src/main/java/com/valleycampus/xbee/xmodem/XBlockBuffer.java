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

package com.valleycampus.xbee.xmodem;

import com.valleycampus.zigbee.io.FrameBuffer;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class XBlockBuffer {
    
    private FrameBuffer rawDataBuffer;
    private int currentBlock = 0;
    
    public XBlockBuffer(byte[] data, int off, int len) {
        rawDataBuffer = new FrameBuffer(data, off, len);
    }
	
	public int getSize() {
		return rawDataBuffer.getPosition();
	}
    
    private int nextBlock() {
		return (currentBlock + 1) & 0xFF;
    }
    
    public XBlock get(boolean extended) {
        int remaining = rawDataBuffer.getRemaining();
        if (remaining == 0) {
            return null;
        }
        XBlock xblock = new XBlock(extended);
		currentBlock = nextBlock();
        xblock.setSequence(currentBlock);
        int actualDataSize = xblock.getDataSize();
        if (remaining < actualDataSize) {
            actualDataSize = remaining;
        }
        byte[] rawData = new byte[actualDataSize];
        rawDataBuffer.get(rawData);
        xblock.setData(rawData);
        return xblock;
    }
    
    public boolean put(XBlock block) {
        if (block.getSequence() != nextBlock()) {
            return false;
        }
        rawDataBuffer.put(block.getData(), 0, block.getDataSize());
		currentBlock = nextBlock();
        return true;
    }
}
