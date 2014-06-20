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

import com.valleycampus.zigbee.util.ArrayFifoQueue;

/**
 * Tweaked version of IBM's WorkQueue example.
 * 
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public class WorkQueue implements Service {

    private final int poolSize;
    private final int queueSize;
    private final PoolWorker[] threads;
    private final ArrayFifoQueue queue;
    private boolean active = false;

    public WorkQueue(int poolSize, int queueSize) {
        this.poolSize = poolSize;
        this.queueSize = queueSize;
        queue = new ArrayFifoQueue(queueSize);
        threads = new PoolWorker[poolSize];
    }
    
    public int getQueueSize() {
        return queueSize;
    }
    
    public int remainingTask() {
        return queue.size();
    }

    public synchronized boolean activate() {
        if (active) {
            return false;
        }
        for (int i = 0; i < poolSize; i++) {
            threads[i] = new PoolWorker();
            threads[i].activate();
        }
        active = true;
        return true;
    }
    
    public synchronized boolean shutdown() {
        if (!active) {
            return false;
        }
        for (int i = 0; i < poolSize; i++) {
            threads[i].shutdown();
        }
        active = false;
        return true;
    }
    
    public void execute(Runnable r) {
        queue.enqueue(r);
    }

    private class PoolWorker extends ServiceTask {

        protected void taskLoop() {
            Runnable r = (Runnable) queue.blockingDequeue();
            try {
                r.run();
            } catch (Throwable t) {
                t.printStackTrace();
                System.out.println("[PoolWorker] Uncaught Exception!");
            }
        }
    }
}