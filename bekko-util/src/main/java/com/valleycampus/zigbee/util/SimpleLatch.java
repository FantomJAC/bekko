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

/**
 *
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public class SimpleLatch implements Latch {
    
    private Object obj;
    private boolean awaiting = false;
    
    public synchronized boolean isAwaiting() {
        return awaiting;
    }

    public Object await() {
        return await(0);
    }

    public synchronized Object await(int timeout) {
        if (awaiting) {
            throw new IllegalStateException("Someone is awaiting this latch.");
        }
        while (obj == null) {
            awaiting = true;
            try {
                wait(timeout);
            } catch (InterruptedException ex) {
            }
            if (timeout != 0 && (obj == null)) {
                awaiting = false;
                return null;
            }
        }
        Object e = get();
        awaiting = false;
        return e;
    }
    
    public synchronized Object get() {
        Object e = obj;
        obj = null;
        return e;
    }

    public synchronized boolean set(Object e) {
        if (obj != null) {
            return false;
        }
        if (e == null) {
            throw new NullPointerException();
        }
        obj = e;
        notify();
        return true;
    }
    
    public synchronized boolean abort() {
        if (awaiting) {
            notify();
            return true;
        }
        return false;
    }
}
