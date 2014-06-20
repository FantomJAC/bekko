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
package com.valleycampus.zigbee.io;

/**
 *
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public abstract class Buffer {

    private int mark;
    private int position;
    private int limit;
    private int capacity;

    public Buffer(int capacity) {
        this.capacity = capacity;
        this.limit = capacity;
        this.mark = 0;
        this.position = 0;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getPosition() {
        return position;
    }

    public Buffer setPosition(int position) {
        if (position < mark || position > getLimit()) {
            throw new IllegalArgumentException("Out of range");
        }
        this.position = position;
        return this;
    }

    public int getLimit() {
        return limit;
    }

    public Buffer setLimit(int limit) {
        if (limit < getPosition() || limit > getCapacity()) {
            throw new IllegalArgumentException("Out of range");
        }
        this.limit = limit;
        return this;
    }

    public Buffer skip(int n) {
        position += n;
        return this;
    }

    public Buffer mark() {
        mark = getPosition();
        return this;
    }
    
    public Buffer reset() {
        setPosition(mark);
        return this;
    }

    public Buffer rewind() {
        mark = 0;
        setPosition(0);
        return this;
    }

    public int getRemaining() {
        return getLimit() - getPosition();
    }

    public Buffer flip() {
        setLimit(getPosition());
        rewind();
        return this;
    }
}
