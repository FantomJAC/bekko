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
    
    private int byteOrder;
    
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
            sb.append(ByteUtil.toHexString(buffer[i]) + " ");
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
    
    public long toInt(byte[] src, int off, int len) {
        long dest = 0;
        if (byteOrder == BO_LE) {
            for (int p = 0; p < len; p++) {
                long d = src[off + p] & 0xff;
                dest |= (d << (BYTE_SIZE * p));
            }
        } else {
            for (int p = 0; p < len; p++) {
                long d = src[off + p] & 0xff;
                dest |= (d << (BYTE_SIZE * (len - 1 - p)));
            } 
        }
        return dest;
    }
    
    public long toInt64(byte[] src, int off) {
        long dest = 0;
        if (byteOrder == BO_LE) {
            for (int p = 0; p < INT_64_SIZE; p++) {
                long d = src[off + p] & 0xff;
                dest |= (d << (BYTE_SIZE * p));
            }
        } else {
            for (int p = 0; p < INT_64_SIZE; p++) {
                long d = src[off + p] & 0xff;
                dest |= (d << (BYTE_SIZE * (INT_64_SIZE - 1 - p)));
            } 
        }
        return dest;
    }
    
    public int toInt16(byte[] src, int off) {
        if (byteOrder == BO_LE) {
            return  (src[off + 0] & 0xFF) |
                    (src[off + 1] & 0xFF) << 8;
        } else {
            return  (src[off + 0] & 0xFF) << 8 |
                    (src[off + 1] & 0xFF);
        }
    }
    
    public int toInt32(byte[] src, int off) {
        if (byteOrder == BO_LE) {
            return  (src[off + 0] & 0xFF)       |
                    (src[off + 1] & 0xFF) << 8  |
                    (src[off + 2] & 0xFF) << 16 |
                    (src[off + 3] & 0xFF) << 24;
        } else {
            return  (src[off + 0] & 0xFF) << 24 |
                    (src[off + 1] & 0xFF) << 16 |
                    (src[off + 2] & 0xFF) << 8  |
                    (src[off + 3] & 0xFF);
        }
    }

    public int[] toInt32Array(byte[] src, int off, int size, int length) {
        int[] dest = new int[length];
        if (byteOrder == BO_LE) {
            for (int index = 0; index < length; index++) {
                dest[index] = 0;
                for (int p = 0; p < size; p++) {
                    int d = src[off + (p + size * index)] & 0xff;
                    dest[index] |= (d << (BYTE_SIZE * p));
                }
            }
        } else {
            for (int index = 0; index < length; index++) {
                dest[index] = 0;
                for (int p = 0; p < size; p++) {
                    int d = src[off + (p + size * index)] & 0xff;
                    dest[index] |= (d << (BYTE_SIZE * (size - 1 - p)));
                }
            }  
        }
        return dest;
    }
    
    public byte[] toByteArray(int[] src, int size, int length) {
        byte[] dest = new byte[size * length];
        if (byteOrder == BO_LE) {
            for (int index = 0; index < length; index++) {
                for (int p = 0; p < size; p++) {
                    dest[p + size * index] = (byte) ((src[index] >> (BYTE_SIZE * p)) & 0xff);
                }
            }
        } else {
            for (int index = 0; index < length; index++) {
                for (int p = 0; p < size; p++) {
                    dest[p + size * index] = (byte) ((src[index] >> (BYTE_SIZE * (size - 1 - p))) & 0xff);
                }
            }
        }
        return dest;
    }

    public byte[] toByteArray(int src, int size) {
        byte[] dest = new byte[size];
        if (byteOrder == BO_LE) {
            for (int p = 0; p < size; p++) {
                dest[p] = (byte) ((src >> (BYTE_SIZE * p)) & 0xff);
            }
        } else {
            for (int p = 0; p < size; p++) {
                dest[p] = (byte) ((src >> (BYTE_SIZE * (size - 1 - p))) & 0xff);
            } 
        }
        return dest;
    }
    
    public byte[] toByteArray(long src, int size) {
        byte[] dest = new byte[size];
        if (byteOrder == BO_LE) {
            for (int p = 0; p < size; p++) {
                dest[p] = (byte) ((src >> (BYTE_SIZE * p)) & 0xffL);
            }
        } else {
            for (int p = 0; p < size; p++) {
                dest[p] = (byte) ((src >> (BYTE_SIZE * (size - 1 - p))) & 0xffL);
            } 
        }
        return dest;
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
