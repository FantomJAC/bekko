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
public class Sequence {
    
    private int sequence = 0;
    private final int mask;
    
    public Sequence(int bits) {
        if (bits < 1) {
            throw new IllegalArgumentException();
        }
        int m = 0x01;
        for (int i = 0; i < (bits - 1); i++) {
            m |= (m << 1);
        }
        mask = m;
    }
    
    public synchronized int nextSequence() {
        int next = sequence;
        sequence = (sequence + 1) & mask;
        return next;
    }
}
