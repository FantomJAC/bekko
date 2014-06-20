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
package com.valleycampus.zigbee.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;

/**
 * 
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public class BufferedPipe {

    private static final int DEFAULT_TIMEOUT = 100;
    private final byte[] buffer;
    private final int bufferSize;
    private int head;
    private int tail;
    private int timeout;
    private final Object pipeLock;
    private final PipeInputStream pis;
    private final PipeOutputStream pos;
    private boolean shutdown = false;

    public BufferedPipe(int bufferSize) {
        this.bufferSize = bufferSize;
        buffer = new byte[bufferSize];
        head = 0;
        tail = 0;
        timeout = DEFAULT_TIMEOUT;
        pipeLock = new Object();
        pis = new PipeInputStream();
        pos = new PipeOutputStream();
    }
    
    public Object getLockObject() {
        return pipeLock;
    }
    
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isEmpty() {
        synchronized (pipeLock) {
            return head == tail;
        }
    }

    public boolean isFull() {
        synchronized (pipeLock) {
            return ((head + 1) % bufferSize) == tail;
        }
    }
    
    public void shutdown(boolean shutdown, boolean clearBuffer) {
        synchronized (pipeLock) {
            if (clearBuffer) {
                head = 0;
                tail = 0;
            }
            this.shutdown = shutdown;
            pipeLock.notifyAll();
        }
    }
    
    public boolean isShutdown() {
        synchronized (pipeLock) {
            return shutdown;
        }
    }

    protected int bufferAvailable() {
        if (isEmpty()) {
            return 0;
        } else {
            synchronized (pipeLock) {
                if (head > tail) {
                    return head - tail;
                } else {
                    return (bufferSize - tail) + head;
                }
            }
        }
    }
    
    public int bufferLeft() {
        return bufferSize - bufferAvailable();
    }

    public InputStream getInputStream() {
        return pis;
    }

    public OutputStream getOutputStream() {
        return pos;
    }
    
    private class PipeInputStream extends InputStream {

        private int awaitingSize = 0;
        
        public int available() {
            return bufferAvailable();
        }

        /**
         * Single read, always block while empty.
         * @return
         * @throws IOException 
         */
        public int read() throws IOException {
            if (isEmpty()) {
                if (isShutdown()) {
                    return -1;
                }
                waitMinIncoming();
                // Shutdown and still buffer is empty.
                if (isShutdown() && isEmpty()) {
                    return -1;
                }
            }

            int b;
            synchronized (pipeLock) {
                b = buffer[tail];
                tail = (tail + 1) % bufferSize;
            }
            return b & 0xFF;
        }

        public int read(byte[] b) throws IOException {
            return read(b, 0, b.length);
        }

        public int read(byte[] b, int off, int length) throws IOException {
            if (b == null) {
                throw new NullPointerException("Null array");
            }
            if (length > (b.length - off)) {
                throw new IndexOutOfBoundsException();
            }

            if (length != 0 && length > available()) {
                if (isEmpty() && isShutdown()) {
                    return -1;
                }
                waitIncoming(length);
                int available = available();
                if (available < length) {
                    length = available;
                }
            }
            
            if (length == 0) {
                if (isShutdown()) {
                    return -1;
                }
                return 0;
            }

            synchronized (pipeLock) {
                if (head > tail) {
                    copy(buffer, tail, b, off, length);
                } else {
                    int front = bufferSize - tail;
                    if (length <= front) {
                        copy(buffer, tail, b, off, length);
                    } else {
                        copy(buffer, tail, b, off, front);
                        copy(buffer, 0, b, off + front, length - front);
                    }
                }
                tail = (tail + length) % bufferSize;
            }
            return length;
        }

        public long skip(long n) {
            if (isEmpty()) {
                return 0;
            } else {
                int available = available();
                if (n > available) {
                    n = available;
                }
                synchronized (pipeLock) {
                    tail = (tail + (int) n) % bufferSize;
                }
                return n;
            }
        }
        
        public void close() {
            // Shutdown, clear buffer, nomore input.
            shutdown(true, true);
        }
        
        private void waitMinIncoming() throws InterruptedIOException {
            synchronized (pipeLock) {
                awaitingSize = 1;
                while (available() == 0 && !shutdown) {
                    try {
                        pipeLock.wait();
                    } catch (InterruptedException ex) {
                        throw new InterruptedIOException("Interrupted while awaiting a byte.");
                    }
                }
            }
        }

        private void waitIncoming(int length) throws InterruptedIOException {
            synchronized (pipeLock) {
                awaitingSize = length;
                try {
                    pipeLock.wait(timeout);
                } catch (InterruptedException ex) {
                    throw new InterruptedIOException("Interrupted while awaiting " + length + " bytes.");
                }
            }
        }
        
        private void notifyIncoming(boolean forceRead) {
            synchronized (pipeLock) {
                if (awaitingSize <= bufferAvailable() || forceRead) {
                    pipeLock.notifyAll();
                }
            }
        }
    }

    private class PipeOutputStream extends OutputStream {

        public void write(int b) throws IOException {
            checkIsShutdownRequested();
            if (!isFull()) {
                synchronized (pipeLock) {
                    buffer[head] = (byte) (b & 0xFF);
                    head = (head + 1) % bufferSize;
                    pis.notifyIncoming(false);
                }
            }
        }

        public void write(byte[] b) throws IOException {
            write(b, 0, b.length);
        }

        public void write(byte[] b, int off, int length) throws IOException {
            checkIsShutdownRequested();
            int space = bufferLeft();
            if (length > space) {
                throw new IOException("No more space left");
            }

            int woff = 0;
            int slot = length;
            synchronized (pipeLock) {
                while (!isFull()) {
                    int count;
                    if (head >= tail) {
                        count = bufferSize - head;
                    } else {
                        count = tail;
                    }
                    if (slot > count) {
                        slot = count;
                    }
                    copy(b, off + woff, buffer, head, slot);
                    head = (head + slot) % bufferSize;
                    woff += slot;
                    slot = length - slot;
                    if (woff == length) {
                        pis.notifyIncoming(false);
                        break;
                    }
                }
            }
        }
        
        public void flush() throws IOException {
            checkIsShutdownRequested();
            if (!isEmpty()) {
                pis.notifyIncoming(true);
            }
        }
        
        public void close() {
            // Shutdown, buffer remains, nomore output.
            shutdown(true, false);
        }
        
        private void checkIsShutdownRequested() throws IOException {
            if (isShutdown()) {
                throw new IOException("Shutdown requested!");
            }
        }
    }

    private static void copy(byte[] src, int srcPos, byte[] dest, int destPos, int length) {
//        rawJEM.bblkcpy(
//                rawJEM.toInt(src) + OBJECT.ARRAY_ELEMENT0 + srcPos,
//                rawJEM.toInt(dest) + OBJECT.ARRAY_ELEMENT0 + destPos,
//                length);
        System.arraycopy(src, srcPos, dest, destPos, length);
    }
}
