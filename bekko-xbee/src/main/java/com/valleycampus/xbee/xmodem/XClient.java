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
package com.valleycampus.xbee.xmodem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.valleycampus.zigbee.io.ByteUtil;
import com.valleycampus.zigbee.io.FrameBuffer;
import com.valleycampus.zigbee.util.CRC16CCITT;
import com.valleycampus.zigbee.util.Commons;

/**
 * XModem Client(Sender)
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class XClient {
    
    public static final byte SOH = 0x01;
    public static final byte EOT = 0x04;
    public static final byte ACK = 0x06;
    public static final byte NAK = 0x15;
    public static final byte CAN = 0x18;
    // 'C' which use in XModem/CRC
    public static final byte C = 0x43;
    public static final int MAX_RETRY = 10;
    
    private InputStream is;
    private OutputStream os;
    private boolean ready;
    private XSendThread currentThread = null;
    private FrameBuffer rawDataBuffer;
    private final Object blockUpdate = new Object();
    private int blockLength;
    private int currentBlock;
    
    public XClient(InputStream is, OutputStream os) {
        this.is = is;
        this.os = os;
    }
    
    public synchronized boolean isReady() {
        return ready;
    }
    
    private synchronized void setReady(boolean ready) {
        this.ready = ready;
    }
    
    public boolean beginSend(byte[] data, int off, int len, long timeout) {
        if (!isReady()) {
            return false;
        }
        setReady(false);
        rawDataBuffer = new FrameBuffer(data, off, len);
        blockLength = (len >>> 7) + ((len & 0x7F) > 0 ? 1 : 0);
        currentThread = new XSendThread();
        currentThread.start();
        return true;
    }
    
    public synchronized int getBlockLength() {
        return blockLength;
    }
    
    public int getCurrentBlock() {
        synchronized (blockUpdate) {
            return currentBlock;
        }
    }
    
    private void updateCurrentBlock(int newBlock) {
        synchronized (blockUpdate) {
            currentBlock = newBlock;
            blockUpdate.notifyAll();
        }
    }
    
    public boolean waitUntilBlockUpdate(long timeout) {
        int old;
        synchronized (blockUpdate) {
            old = currentBlock;
            try {
                blockUpdate.wait(timeout);
            } catch (InterruptedException ex) {
            }
            return old != currentBlock;
        }
    }
    
    private class XSendThread extends Thread {

        private boolean crcMode;
        private CRC16CCITT crc16;
        private byte[] readBuffer = new byte[256];
        private byte[] writeBuffer = new byte[256];
        private FrameBuffer writeFrameBuffer = new FrameBuffer(writeBuffer);
        
        public XSendThread() {
            writeFrameBuffer.setByteOrder(FrameBuffer.BO_BIG_ENDIAN);
        }
        
        private byte receive(boolean ack, long timeout) throws IOException {
            long startTime = System.currentTimeMillis();
            while (true) {
                int r = is.read(readBuffer, 0, 1);
                if (r > 0) {
                    byte b = readBuffer[0];
                    if ((!ack && b == C) || b == CAN || b == NAK || (ack && (b == ACK))) {
                        System.out.println(">>" + ByteUtil.toHexString((byte) b));
                        return b;
                    }
                }
                long elapsedTime = System.currentTimeMillis() - startTime;
                if (elapsedTime > timeout) {
                    return -1;
                } else {
                    continue;
                }
            }
        }
        
        private void send() throws IOException {
            for (int i = 0; i < MAX_RETRY; i++) {
                System.out.println("Send #" + i);
                Commons.printDev(writeFrameBuffer.getRawArray(),
                        writeFrameBuffer.getOffset(),
                        writeFrameBuffer.getPosition(), false);
                os.write(writeFrameBuffer.getRawArray(),
                        writeFrameBuffer.getOffset(),
                        writeFrameBuffer.getPosition());
                os.flush();
                byte b = receive(true, 10000);
                if (b == CAN) {
                    throw new IOException("Cancel");
                } else if (b == ACK) {
                    return;
                }
            }
            throw new IOException("Timeout");
        }
        
        private void addCRC() {
            if (crcMode) {
                crc16.reset(0);
                for (int i = 0; i < XBlock.DATA_SIZE; i++) {
                    crc16.update(writeBuffer[3 + i]);
                }
                writeFrameBuffer.putInt16(crc16.getCRC());
            } else {
                int cs = 0;
                for (int i = 0; i < XBlock.DATA_SIZE; i++) {
                    cs += writeBuffer[3 + i];
                }
                writeFrameBuffer.putInt8(cs);
            }
        }
        
        public void run() {
            try {
                System.out.println("XModem Send Transcation Start...");
                byte b = receive(false, 60000);
                if (b == CAN) {
                    System.out.println("Cancel!!");
                    return;
                } else if (b == -1) {
                    System.out.println("60sec Timeout!");
                    return;
                }
                if (b == C) {
                    System.out.println("XModem/CRC Detect");
                    crcMode = true;
                    crc16 = new CRC16CCITT(false);
                } else if (b == NAK) {
                    System.out.println("XModem/Checsum Detect");
                    crcMode = false;
                }
                System.out.println("Begin...");
                for (int block = 0; block < getBlockLength(); block++) {
                    System.out.println("Fill Block #" + block);
                    updateCurrentBlock(block);
                    // Fill block
                    XBlock xblock = new XBlock();
                    xblock.setSequence((byte) (block + 1));
                    int blockLen = XBlock.DATA_SIZE;
                    if (rawDataBuffer.getRemaining() < XBlock.DATA_SIZE) {
                        blockLen = rawDataBuffer.getRemaining();
                    }
                    byte[] rawBlock = new byte[blockLen];
                    rawDataBuffer.get(rawBlock);
                    xblock.setData(rawBlock);
                    // Fill Buffer
                    writeFrameBuffer.rewind();
                    writeFrameBuffer.put(SOH);
                    xblock.pull(writeFrameBuffer);
                    addCRC();
                    // Send Buffer
                    send();
                }
                System.out.println("End Stream");
                writeFrameBuffer.rewind();
                writeFrameBuffer.put(EOT);
                send();
                System.out.println("Transaction Done.");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                Thread.sleep(1000);
                int a = is.available();
                System.out.println("AVL: " + a);
                is.read(readBuffer, 0, a);
                System.out.println(new String(readBuffer, 0, a));
            } catch (Exception ex) {
            }
            setReady(true);
        }
    }
}
