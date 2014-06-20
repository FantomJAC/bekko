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

import java.io.IOException;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public interface XBeeAPI {

    public static final int VR_TYPE_MASK = 0x0F00;
    public static final int VR_COORDINATOR = 0x0100;
    public static final int VR_ROUTER = 0x0300;
    public static final int VR_END_DEVICE = 0x0900;
    public static final int VR_FIRM_MASK = 0xF000;
    public static final int VR_ZN = 0x1000;
    public static final int VR_ZB = 0x2000;
    public static final int VR_SE = 0x3000;
    public static final int HV_MASK = 0xFF00;
    public static final int HV_S1 = 0x1700;
    public static final int HV_S1_PRO = 0x1800;
    public static final int HV_S2 = 0x1900;
    public static final int HV_S2_PRO = 0x1A00;
    public static final int HV_S2B_PRO = 0x1E00;
    public static final int HV_S6 = 0x1F00;

    public void addAPIListener(XBeeAPIListener listener);

    public XBeeResponse syncSubmit(XBeeRequest request, int timeout) throws IOException;
    
    public int asyncSubmit(XBeeRequest request, boolean needResponse);
}
