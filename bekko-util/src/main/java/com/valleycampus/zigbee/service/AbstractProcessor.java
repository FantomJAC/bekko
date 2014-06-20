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

import java.util.ArrayList;
import java.util.List;
import com.valleycampus.zigbee.util.ArrayFifoQueue;
import com.valleycampus.zigbee.util.BlockingFifoQueue;

/**
 *
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public abstract class AbstractProcessor implements Processor {

    private BlockingFifoQueue queue;
    private ServiceTask processTask = null;
    private List listenerList;
    private boolean nonBlockingFire;

    public AbstractProcessor(int queueSize) {
        queue = new ArrayFifoQueue(queueSize);
        listenerList = new ArrayList();
        processTask = new ProcessTask();
    }

    public void process(Object o) {
        queue.blockingEnqueue(o);
    }

    public void addListener(ProcessListener listener) {
        listenerList.add(listener);
    }

    public synchronized boolean activate() {
        return processTask.activate();
    }
    
    public synchronized boolean shutdown() {
        return processTask.shutdown();
    }

    protected abstract Object processNext(Object o) throws Exception;
    
    protected BlockingFifoQueue getRawQueue() {
        return queue;
    }

    private void fireDone(final Object o, final Object result, boolean block) {
        for (int i = 0; i < listenerList.size(); i++) {
            final ProcessListener listener = (ProcessListener) listenerList.get(i);
            Runnable r = new Runnable() {
                public void run() {
                    listener.done(o, result);
                }
            };
            if (!block) {
                new Thread(r).start();
            } else {
                r.run();
            }
        }
    }

    private void fireFailed(final Object o, final Object reason, boolean nonBlock) {
        for (int i = 0; i < listenerList.size(); i++) {
            final ProcessListener listener = (ProcessListener) listenerList.get(i);
            Runnable r = new Runnable() {
                public void run() {
                    listener.failed(o, reason);
                }
            };
            if (nonBlock) {
                new Thread(r).start();
            } else {
                r.run();
            }
        }
    }

    /**
     * @return the nonBlockingFire
     */
    public boolean isNonBlockingFire() {
        return nonBlockingFire;
    }

    /**
     * @param nonBlockingFire the nonBlockingFire to set
     */
    public void setNonBlockingFire(boolean nonBlockingFire) {
        this.nonBlockingFire = nonBlockingFire;
    }

    private class ProcessTask extends ServiceTask {

        public void taskLoop() {
            Object o = queue.blockingDequeue();
            if (o == null) {
                return;
            }
            try {
                Object result = processNext(o);
                fireDone(o, result, isNonBlockingFire());
            } catch (Exception ex) {
                fireFailed(o, ex, isNonBlockingFire());
            }
        }
    }
}
