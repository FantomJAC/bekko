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
public class ByteUtil {
    
    private static final int BO_LE = 0;
    private static final int BO_BE = 1;
    
    public static final ByteUtil LITTLE_ENDIAN = new ByteUtil(BO_LE);
    public static final ByteUtil BIG_ENDIAN = new ByteUtil(BO_BE);
    
    public static final int INT_64_SIZE = 8;
    public static final int INT_32_SIZE = 4;
    public static final int INT_16_SIZE = 2;
    public static final int INT_8_SIZE = 1;
    public static final int BYTE_SIZE = 8;
    
    private final int byteOrder;
    
    private ByteUtil(int byteOrder) {
        this.byteOrder = byteOrder;
    }
    
    public static int hashcode(byte[] a) {
        if (a == null) {
            return 0;
        }

        int hash = 1;
        for (int i = 0; i < a.length; i++) {
            hash = 31 * hash + a[i];
        }
        
        return hash;
    }
    
    public static String toHexString(byte b) {
        String hex = Integer.toHexString(b);
        int len = hex.length();
        if (len == 2) {
            return hex;
        } else if (len == 1) {
            return "0" + hex;
        } else {
            return hex.substring(len - 2);
        }
    }
    
    public static String toHexString(short s) {
        return toHexString((byte) (s >> 8)) + toHexString((byte) s);
    }

    public static String toHexString(byte[] data, int offset, int length) {
        String s = "";
        for (int i = offset; i < offset + length; i++) {
            s += toHexString(data[i]);
        }
        return s;
    }
    
    public static String toString(byte[] buffer, int off, int len) {
        StringBuffer sb = new StringBuffer();
        for (int i = off; i < off + len; i++) {
            sb.append(ByteUtil.toHexString(buffer[i])).append(" ");
        }
        return sb.toString();
    }
    
    public static String toString(byte[] buffer, int off, int len, boolean resp) {
        if (resp) {
            return ">>(" + len + ") " + toString(buffer, off, len);
        } else {
            return "<<(" + len + ") " + toString(buffer, off, len);
        }
    }
    
    private long toInt(byte[] src, int off, int size, int index) {
        long dest = 0;
        for (int p = 0; p < size; p++) {
            long d = src[off + (p + size * index)] & 0xffL;
            if (byteOrder == BO_LE) {
                dest |= (d << (BYTE_SIZE * p));
            } else {
                dest |= (d << (BYTE_SIZE * (size - 1 - p)));
            }
        }
        return dest;
    }
    
    public long toInt(byte[] src, int off, int size) {
        return toInt(src, off, size, 0);
    }

    public long toInt64(byte[] src, int off) {
        return toInt(src, off, INT_64_SIZE);
    }
    
    public int toInt16(byte[] src, int off) {
        return (int) toInt(src, off, INT_16_SIZE);
    }
    
    public int toInt32(byte[] src, int off) {
        return (int) toInt(src, off, INT_32_SIZE);
    }
    
    public int[] toInt32Array(byte[] src, int off, int size, int length) {
        int[] dest = new int[length];
        for (int index = 0; index < length; index++) {
            dest[index] = (int) toInt(src, off, size, index);
        }
        return dest;
    }
    
    public byte[] toByteArray(int[] src, int size, int length) {
        byte[] dest = new byte[size * length];
        for (int index = 0; index < length; index++) {
            for (int p = 0; p < size; p++) {
                if (byteOrder == BO_LE) {
                    dest[p + size * index] = (byte) ((src[index] >> (BYTE_SIZE * p)) & 0xff);
                } else {
                    dest[p + size * index] = (byte) ((src[index] >> (BYTE_SIZE * (size - 1 - p))) & 0xff);
                }
            }
        }
        return dest;
    }

    public byte[] toByteArray(int src, int size) {
        return toByteArray(new int[] {src}, size, 1);
    }
    
    public byte[] toByteArray(long src, int size) {
        return toByteArray(new int[] {(int) (src >> 32), (int) src}, size, 2);
    }
    
    public byte[] toByteArray(String src) {
        if ((src.length() % 2) != 0) {
            src = "0" + src;
        }
        byte[] a = src.getBytes();
        byte[] dest = new byte[src.length() / 2];
        
        int destP;
        if (byteOrder == BO_BE) {
            destP = 0;
        } else {
            destP = dest.length - 1;
        }
        
        for (int i = 0; i < src.length(); i += 2) {
            byte b = (byte) Integer.parseInt(new String(a, i, 2), 16);
            if (byteOrder == BO_BE) {
                dest[destP++] = b;
            } else {
                dest[destP--] = b;
            }
        }
        
        return dest;
    }
}
