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
package com.valleycampus.zigbee.service;

/**
 *
 * @deprecated 
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public abstract class ServiceThread extends Thread implements Service {

    private boolean active = false;
    private volatile boolean shutdownRequested;
    private final Object activeLock = new Object();
    
    public ServiceThread(String name) {
        super(name);
    }
    
    public ServiceThread() {
    }

    public boolean activate() {
        synchronized (activeLock) {
            if (active) {
                return false;
            }
            active = true;
            shutdownRequested = false;
        }
        this.start();
        return true;
    }

    public boolean shutdown() {
        shutdownRequested = true;
        return true;
    }
    
    public boolean isActive() {
        synchronized (activeLock) {
            return active;
        }
    }
    
    public boolean shutdownAndWait(int timeout) {
        shutdown();
        return waitForShutdown(timeout);
    }

    public boolean waitForShutdown(int timeout) {
        synchronized (activeLock) {
            if (active) {
                try {
                    activeLock.wait(timeout);
                } catch (InterruptedException ex) {
                }
            }
            return !active;
        }
    }

    public final void run() {
        while (!shutdownRequested) {
            try {
                taskLoop();
            } catch (Exception ex) {
                System.err.println("[ServiceThread] Uncaught exception: " + ex);
                ex.printStackTrace();
            } catch (Throwable t) {
                System.err.println("[ServiceThread] Uncaught error: " + t);
                t.printStackTrace();
            }
        }
        
        synchronized (activeLock) {
            active = false;
            activeLock.notifyAll();
        }
    }

    protected abstract void taskLoop();
}
