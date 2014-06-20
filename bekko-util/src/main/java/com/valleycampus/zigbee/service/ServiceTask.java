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
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public abstract class ServiceTask implements Service, Runnable {

    private volatile boolean shutdownRequested = false;
    private volatile Thread context = null;
    private final String contextName;
    private int contextPriority = -1;
    
    public ServiceTask(String name, int priority) {
        this.contextName = name;
        this.contextPriority = priority;
    }
    
    public ServiceTask(String name) {
        this(null, Thread.NORM_PRIORITY);
    }
    
    public ServiceTask() {
        this(null);
    }
    
    protected Thread getContext() {
        return context;
    }

    public boolean activate() {
        if (context != null) {
            return false;
        }
        if (contextName != null) {
            context = new Thread(this, contextName);
        } else {
            context = new Thread(this);
        }
        if (contextPriority != -1) {
            context.setPriority(contextPriority);
        }
        context.start();
        return true;
    }

    public boolean shutdown() {
        shutdownRequested = true;
        context.interrupt();
        return true;
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
    }
    
    protected void taskEnd() {
        shutdownRequested = true;
    }

    protected abstract void taskLoop();
}
