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
package com.valleycampus.zigbee.zdp.command;

import com.valleycampus.zigbee.io.Frame;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public interface ZDPCommand extends Frame {
    
    public final static byte STATUS_SUCCESS = 0x00;
    public final static byte STATUS_INV_REQUESTTYPE = (byte) 0x80;
    public final static byte STATUS_DEVICE_NOT_FOUND = (byte) 0x81;
    public final static byte STATUS_INVALID_EP = (byte) 0x82;
    public final static byte STATUS_NOT_ACTIVE = (byte) 0x83;
    public final static byte STATUS_NOT_SUPPORTED = (byte) 0x84;
    public final static byte STATUS_TIMEOUT = (byte) 0x85;
    public final static byte STATUS_NO_MATCH = (byte) 0x86;
    public final static byte STATUS_NO_ENTRY = (byte) 0x88;
    public final static byte STATUS_NO_DESCRIPTOR = (byte) 0x89;
    public final static byte STATUS_INSUFFICIENT_SPACE = (byte) 0x8a;
    public final static byte STATUS_NOT_PERMITTED = (byte) 0x8b;
    public final static byte STATUS_TABLE_FULL = (byte) 0x8c;
    public final static byte STATUS_NOT_AUTHORIZED = (byte) 0x8d;
    public final static short ZDP_NWK_ADDR_REQ = (short) 0x0000;
    public final static short ZDP_NWK_ADDR_RSP = (short) 0x8000;
    public final static short ZDP_IEEE_ADDR_REQ = (short) 0x0001;
    public final static short ZDP_IEEE_ADDR_RSP = (short) 0x8001;
    public final static short ZDP_NODE_DESC_REQ = (short) 0x0002;
    public final static short ZDP_NODE_DESC_RSP = (short) 0x8002;
    public final static short ZDP_POWER_DESC_REQ = (short) 0x0003;
    public final static short ZDP_POWER_DESC_RSP = (short) 0x8003;
    public final static short ZDP_SIMPLE_DESC_REQ = (short) 0x0004;
    public final static short ZDP_SIMPLE_DESC_RSP = (short) 0x8004;
    public final static short ZDP_ACTIVE_EP_REQ = (short) 0x0005;
    public final static short ZDP_ACTIVE_EP_RSP = (short) 0x8005;
    public final static short ZDP_MATCH_DESC_REQ = (short) 0x0006;
    public final static short ZDP_MATCH_DESC_RSP = (short) 0x8006;
    public final static short ZDP_COMPLEX_DESC_REQ = (short) 0x0010;
    public final static short ZDP_COMPLEX_DESC_RSP = (short) 0x8010;
    public final static short ZDP_USER_DESC_REQ = (short) 0x0011;
    public final static short ZDP_USER_DESC_RSP = (short) 0x8011;
    public final static short ZDP_DEVICE_ANNCE = (short) 0x0013;
    public final static short ZDP_END_DEVICE_BIND_REQ = (short) 0x0020;
    public final static short ZDP_END_DEVICE_BIND_RSP = (short) 0x8020;
    public final static short ZDP_BIND_REQ = (short) 0x0021;
    public final static short ZDP_BIND_RSP = (short) 0x8021;
    public final static short ZDP_UNBIND_REQ = (short) 0x0022;
    public final static short ZDP_UNBIND_RSP = (short) 0x8022;
    public final static short ZDP_MGMT_LEAVE_REQ = (short) 0x0034;
    public final static short ZDP_MGMT_LEAVE_RSP = (short) 0x8034;
}
