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

import com.valleycampus.zigbee.io.ByteUtil;
import com.valleycampus.zigbee.io.FrameBuffer;
import com.valleycampus.zigbee.util.CRC16CCITT;
import com.valleycampus.zigbee.util.Commons;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class XModem {
	
    public static final byte SOH = 0x01;
    public static final byte STX = 0x02;
    public static final byte EOT = 0x04;
    public static final byte ACK = 0x06;
    public static final byte NAK = 0x15;
    public static final byte CAN = 0x18;
    public static final byte C = 0x43;	// 'C' which use in XModem/CRC
	public static final int MAX_TIMEOUT = 90000;

	private InputStream is;
	private OutputStream os;
	private boolean crcMode;
	private CRC16CCITT crc16;
	private byte[] readBuffer = new byte[XBlock.EXTENDED_DATA_SIZE + 5];
	private byte[] writeBuffer = new byte[XBlock.EXTENDED_DATA_SIZE + 5];
	private FrameBuffer writeFrameBuffer = new FrameBuffer(writeBuffer);

	public XModem(InputStream is, OutputStream os) {
		this.is = is;
		this.os = os;
		writeFrameBuffer.setByteOrder(FrameBuffer.BO_BIG_ENDIAN);
	}

	public void setCRC16Enabled(boolean crcMode) {
		this.crcMode = crcMode;
		if (crcMode && crc16 == null) {
			crc16 = new CRC16CCITT(false);
		}
	}

	private int calculateCRC(XBlock block) {
		byte[] data = block.getData();
		if (crcMode) {
			crc16.reset(0);
			for (int i = 0; i < block.getDataSize(); i++) {
				crc16.update(i < data.length ? data[i] : XBlock.CTRL_Z);
			}
			return crc16.getCRC();
		} else {
			int cs = 0;
			for (int i = 0; i < block.getDataSize(); i++) {
				cs += i < data.length ? data[i] : XBlock.CTRL_Z;
			}
			return cs & 0xFF;
		}
	}

	public int receiveByte() throws IOException {
		long startTime = System.currentTimeMillis();
		while (true) {
			int r = is.read(readBuffer, 0, 1);
			if (r > 0) {
				byte b = readBuffer[0];
				if (
						b == SOH || 
						b == STX || 
						b == EOT || 
						b == ACK || 
						b == NAK ||
						b == CAN ||
						b == C
					) {
					System.out.println(">>" + ByteUtil.toHexString((byte) b));
					return b & 0xFF;
				}
			}
			long elapsedTime = System.currentTimeMillis() - startTime;
			if (elapsedTime > MAX_TIMEOUT) {
				return -1;
			}
		}
	}
	
	public void sendByte(byte b) throws IOException {
		os.write(b);
		os.flush();
	}
	
	public XBlock receiveBlock(boolean extended) throws IOException {
		XBlock block = new XBlock(extended);
		int len = block.quote();
		int r = is.read(readBuffer, 0, len + (crcMode ? 2 : 1));
		block.drain(new FrameBuffer(readBuffer, 0, r));
		int crc;
		if (crcMode) {
			crc = ByteUtil.BIG_ENDIAN.toInt16(readBuffer, len);
		} else {
			crc = readBuffer[len] & 0xFF;
		}
		if (calculateCRC(block) != crc) {
			return null;
		}
		return block;
	}

	public void sendBlock(XBlock block) throws IOException {
		writeFrameBuffer.rewind();
		block.pull(writeFrameBuffer);
		int crc = calculateCRC(block);
		if (crcMode) {
			writeFrameBuffer.putInt16(crc);
		} else {
			writeFrameBuffer.putInt8(crc);
		}
		// Send Buffer
		Commons.printDev(writeFrameBuffer.getRawArray(),
				writeFrameBuffer.getOffset(),
				writeFrameBuffer.getPosition(), false);
		os.write(writeFrameBuffer.getRawArray(),
				writeFrameBuffer.getOffset(),
				writeFrameBuffer.getPosition());
		os.flush();
	}
}
