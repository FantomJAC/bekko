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
package com.valleycampus.zigbee;

/**
 * ZigBee common constants
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public interface ZigBeeConst {

    public static final byte SUCCESS = 0x00;
    public static final byte ASDU_TOO_LONG = (byte) 0xa0;
    public static final byte DEFRAG_DEFERRED = (byte) 0xa1;
    public static final byte DEFRAG_UNSUPPORTED = (byte) 0xa2;
    public static final byte ILLEGAL_REQUEST = (byte) 0xa3;
    public static final byte INVALID_BINDING = (byte) 0xa4;
    public static final byte INVALID_GROUP = (byte) 0xa5;
    public static final byte INVALID_PARAMETER = (byte) 0xa6;
    public static final byte NO_ACK = (byte) 0xa7;
    public static final byte NO_BOUND_DEVICE = (byte) 0xa8;
    public static final byte NO_SHORT_ADDRESS = (byte) 0xa9;
    public static final byte NOT_SUPPORTED = (byte) 0xaa;
    public static final byte SECURED_LINK_KEY = (byte) 0xab;
    public static final byte SECURED_NWK_KEY = (byte) 0xac;
    public static final byte SECURITY_FAIL = (byte) 0xad;
    public static final byte TABLE_FULL = (byte) 0xae;
    public static final byte UNSECURED = (byte) 0xaf;
    public static final byte UNSUPPORTED_ATTRIBUTE = (byte) 0xb0;
    public static final short PROFILE_ZDP = (short) 0x0000;
    public static final short PROFILE_HA = (short) 0x0104;
    public static final short PROFILE_SE = (short) 0x0109;
    public static final int ZDO_ENDPOINT = 0;
    public static final int BRODCAST_ENDPOINT = 0xFF;
}
