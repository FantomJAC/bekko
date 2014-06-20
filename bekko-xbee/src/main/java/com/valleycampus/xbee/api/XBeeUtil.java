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

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class XBeeUtil {
    
    public static byte calculateChecksum(byte[] data, int offset, int length) {
        int c = 0;
        for (int i = offset; i < offset + length; i++) {
            c += data[i] & 0xFF;
        }
        return (byte) (~c & 0xFF);
    }
    
    public static int measureScanDuration(int channels, int sd) {
        return (int) (channels * (0x01 << sd) * 15.36);
    }
}
