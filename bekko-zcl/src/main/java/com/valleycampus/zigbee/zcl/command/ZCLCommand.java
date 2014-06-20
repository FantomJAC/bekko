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
package com.valleycampus.zigbee.zcl.command;

import com.valleycampus.zigbee.io.Frame;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public interface ZCLCommand extends Frame {
    
    public final static byte READ_ATTRIBUTES = 0x00;
    public final static byte READ_ATTRIBUTES_RSP = 0x01;
    public final static byte WRITE_ATTRIBUTES = 0x02;
    public final static byte WRITE_ATTRIBUTES_UNDIVIDED = 0x03;
    public final static byte WRITE_ATTRIBUTES_RSP = 0x04;
    public final static byte WRITE_ATTRIBUTES_NO_RSP = 0x05;
    public final static byte CONFIGURE_REPORTING = 0x06;
    public final static byte CONFIGURE_REPORTING_RSP = 0x07;
    public final static byte READ_REPORTING_CONFIGURATION = 0x08;
    public final static byte READ_REPORTING_CONFIGURATION_RSP = 0x09;
    public final static byte REPORT_ATTRIBUTES = 0x0A;
    public final static byte DEFAULT_RSP = 0x0B;
    public final static byte DISCOVER_ATTRIBUTES = 0x0C;
    public final static byte DISCOVER_ATTRIBUTES_RSP = 0x0D;
    public final static byte READ_ATTRIBUTES_STRUCTURED = 0x0E;
    public final static byte WRITE_ATTRIBUTES_STRUCTURED = 0x0F;
    public final static byte WRITE_ATTRIBUTES_STRUCTURED_RSP = 0x10;
    
    public byte getCommandId();
}
