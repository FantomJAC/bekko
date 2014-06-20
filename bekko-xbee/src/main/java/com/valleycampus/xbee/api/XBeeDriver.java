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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Vector;
import com.valleycampus.zigbee.io.FrameBuffer;
import com.valleycampus.zigbee.io.ByteUtil;
import com.valleycampus.zigbee.service.ServiceTask;
import com.valleycampus.zigbee.service.WorkQueue;
import com.valleycampus.zigbee.util.ArrayFifoQueue;
import com.valleycampus.zigbee.util.SimpleLatch;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class XBeeDriver implements XBeeAPI {

    public static final int BUFFER_SIZE = 512;
    private InputStream is;
    private OutputStream os;
    private FrameBuffer writeBuffer;
    private byte[] readBuffer;
    private int callback = XBeeRequest.DEFAULT_FRAME_ID;
    private Vector listenerList;
    private SimpleLatch[] respLatch;
    private WorkQueue eventQueue;
    private ArrayFifoQueue txQueue;
    private ServiceTask txDispatchThread;
    private ServiceTask rxEventThread;
    private static PrintStream logStream = System.out;
    private static volatile boolean debugEnabled;
    private static volatile boolean traceEnabled;

    public XBeeDriver(InputStream is, OutputStream os) {
        this.is = is;
        this.os = os;
        writeBuffer = new FrameBuffer(new byte[BUFFER_SIZE]);
        readBuffer = new byte[BUFFER_SIZE];
        listenerList = new Vector();
        respLatch = new SimpleLatch[0xFF];
        for (int i = 0; i < 0xFF; i++) {
            respLatch[i] = new SimpleLatch();
        }
        eventQueue = new WorkQueue(1, 10);
        txQueue = new ArrayFifoQueue(10);
    }
    
    public static void setLogStream(PrintStream logStream) {
        XBeeDriver.logStream = logStream;
    }

    public static void setDebugEnabled(boolean debugEnabled) {
        XBeeDriver.debugEnabled = debugEnabled;
    }
    
    public static void setTraceEnabled(boolean traceEnabled) {
        XBeeDriver.traceEnabled = traceEnabled;
    }
    
    protected static void debug(String str) {
        if (debugEnabled) {
            logStream.println("[XBee#debug] " + str);
        }
    }
    
    protected static void trace(byte[] buffer, int off, int len, boolean resp) {
        if (traceEnabled) {
            logStream.println("[XBee#trace] " + ByteUtil.toString(buffer, off, len, resp));
        }
    }
    
    protected static void warn(String str) {
        logStream.println("[XBee#warn] " + str);
    }
    
    public int getTXQueueSize() {
        return txQueue.size();
    }
    
    public int getEventRemainingSize() {
        return eventQueue.remainingTask();
    }

    public void activate() {
        if (txDispatchThread != null) {
            return;
        }

        eventQueue.activate();
        txDispatchThread = new TXDispatchThread();
        rxEventThread = new RXEventThread();
        txDispatchThread.activate();
        rxEventThread.activate();
    }

    public void shutdown() throws IOException {
        if (txDispatchThread == null) {
            return;
        }
        
        debug("TX Shutdown");
        txDispatchThread.shutdown();
        debug("RX Shutdown");
        rxEventThread.shutdown();
//        logger.trace("TX Close");
//        os.close();
//        logger.trace("RX Close");
//        is.close();
        debug("Drain Latch");
        for (int i = 0; i < 0xFF; i++) {
            respLatch[i].get();
        }
        debug("Queue Shutdown");
        eventQueue.shutdown();
    }

    private int nextCallback() {
        if (callback > 255 || callback == 0) {
            callback = XBeeRequest.DEFAULT_FRAME_ID;
        }
        return callback++;
    }

    public void addAPIListener(XBeeAPIListener listener) {
        listenerList.add(listener);
    }

    public XBeeResponse syncSubmit(XBeeRequest request, int timeout) throws IOException {
        int frameID = nextCallback();
        request.setFrameId(frameID);
        txQueue.enqueue(request);
        Object o = respLatch[frameID - 1].await(timeout);
        if (o == null) {
            throw new IOException("XBee Response Timeout");
        }
        XBeeFrameIdResponse response = (XBeeFrameIdResponse) o;
        if (response.getFrameId() != frameID) {
            throw new IOException("Frame ID not match !");
        }
        return response;
    }

    public int asyncSubmit(XBeeRequest request, boolean needResponse) {
        final int frameID;
        if (needResponse) {
            frameID = nextCallback();
        } else {
            frameID = XBeeRequest.NO_RESPONSE_FRAME_ID;
        }
        request.setFrameId(frameID);
        txQueue.enqueue(request);
        return frameID;
    }

    private void rawReceive(final XBeeResponse response) {
        if (response instanceof XBeeFrameIdResponse) {
            XBeeFrameIdResponse rx = (XBeeFrameIdResponse) response;
            SimpleLatch latch = respLatch[rx.getFrameId() - 1];
            if (latch.isAwaiting()) {
                latch.set(response);
                return;
            }
        }
        eventQueue.execute(new Runnable() {
            public void run() {
                for (int i = 0; i < listenerList.size(); i++) {
                    XBeeAPIListener listener = (XBeeAPIListener) listenerList.get(i);
                    listener.handleResponse(response);
                }
            }
        });
    }

    private class TXDispatchThread extends ServiceTask {
        
        public TXDispatchThread() {
            super("XBee TX Dispatcher", Thread.NORM_PRIORITY + 1);
        }

        public void taskLoop() {
            XBeeRequest request = (XBeeRequest) txQueue.blockingDequeue();
            try {
                writeBuffer.rewind();
                writeBuffer.setByteOrder(FrameBuffer.BO_BIG_ENDIAN);
                writeBuffer.put(XBeeFrame.FRAME_DELIMITER);
                int length = request.quote();
                writeBuffer.putInt16(length);
                request.pull(writeBuffer);
                byte cs = XBeeUtil.calculateChecksum(writeBuffer.getRawArray(),
                        writeBuffer.getOffset() + 3, length);
                writeBuffer.put(cs);
                debug("Write reqest");
                trace(writeBuffer.getRawArray(), writeBuffer.getOffset(), length, false);
                os.write(writeBuffer.getRawArray(),
                        writeBuffer.getOffset(),
                        writeBuffer.getPosition());
                os.flush();
            } catch (Exception ex) {
                warn("TXDispatchError");
            }
        }
    }

    private class RXEventThread extends ServiceTask {
        
        private DataInputStream dis = new DataInputStream(is);
        
        public RXEventThread() {
            super("XBee RX Dispatcher", Thread.NORM_PRIORITY + 1);
        }

        public void taskLoop() {
            readBuffer[0] = 0;
            try {
                debug("Read Delimiter");
                dis.readFully(readBuffer, 0, 1);
                if (readBuffer[0] == XBeeFrame.FRAME_DELIMITER) {
                    debug("Read Length");
                    dis.readFully(readBuffer, 1, 2);
                    int length = ByteUtil.BIG_ENDIAN.toInt16(readBuffer, 1);
                    debug("Read Payload + checksum");
                    dis.readFully(readBuffer, 3, length + 1);
                    trace(readBuffer, 0, length + 4, true) ;
                    // Make sure frame is valid
                    byte checksum = XBeeUtil.calculateChecksum(readBuffer, 3, length + 1);
                    if (checksum == 0) {
                        FrameBuffer frameBuffer = new FrameBuffer(readBuffer, 3, length);
                        // Peek first octet to detect frame type.
                        byte frameType = frameBuffer.peek();
                        XBeeResponse response = XBeeResponse.createXBeeResponse(frameType);
                        if (response != null) {
                            response.drain(frameBuffer);
                            rawReceive(response);
                        }
                    } else {
                        warn("Checksum Invalid");
                    }
                }
            } catch (Exception ex) {
                warn("RXEventError");
            }
        }
    }
}
