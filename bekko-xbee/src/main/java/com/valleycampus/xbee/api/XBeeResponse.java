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

import com.valleycampus.xbee.api.common.ATCommandResponse;
import com.valleycampus.xbee.api.s2.RemoteCommandResponse;
import com.valleycampus.xbee.api.common.ModemStatus;
import com.valleycampus.zigbee.io.FrameBuffer;
import com.valleycampus.xbee.api.s1.ReceivePacket16;
import com.valleycampus.xbee.api.s1.ReceivePacket64;
import com.valleycampus.xbee.api.s1.TransmitStatus;
import com.valleycampus.xbee.api.s2.ManyToOneRouteRequestIndicator;
import com.valleycampus.xbee.api.s2.NodeIdentificationIndicator;
import com.valleycampus.xbee.api.s2.RouteRecordIndicator;
import com.valleycampus.xbee.api.s2.ZigBeeExplicitRxIndicator;
import com.valleycampus.xbee.api.s2.ZigBeeIODataSampleRXIndicator;
import com.valleycampus.xbee.api.s2.ZigBeeReceivePacket;
import com.valleycampus.xbee.api.s2.ZigBeeTransmitStatus;
import com.valleycampus.xbee.api.s2.se.ZigBeeDeviceAuthenticatedIndicator;
import com.valleycampus.xbee.api.s2.se.ZigBeeRegisterJoiningDeviceStatus;
import com.valleycampus.xbee.api.s6.ReceivePacketIPv4;
import com.valleycampus.xbee.api.s8.AggregateAddressingUpdate;
import com.valleycampus.xbee.api.s8.RouteInformationPacket;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class XBeeResponse extends XBeeFrame {
    
    public final void pull(FrameBuffer frameBuffer) {
        throw new UnsupportedOperationException("Drain Only");
    }

    public int quote() {
        throw new UnsupportedOperationException("Not Supported");
    }

    public static XBeeResponse createXBeeResponse(byte frameType) {
        switch (frameType) {
        case API_MODEM_STATUS:
            return new ModemStatus();
        case API_AT_COMMAND_RESP:
            return new ATCommandResponse();
        case API_REMOTE_COMMAND_RESP:
            return new RemoteCommandResponse();
        case API_ZB_IOD_SAMPLE_RX_IND:
            return new ZigBeeIODataSampleRXIndicator();
        case API_NODE_ID_IND:
            return new NodeIdentificationIndicator();
        case API_ZB_RECEIVE_PACKET:
            return new ZigBeeReceivePacket();
        case API_RX_PACKET_64:
            return new ReceivePacket64();
        case API_RX_PACKET_16:
            return new ReceivePacket16();
        case API_TX_STATUS:
            return new TransmitStatus();
        case API_ZB_EXPLICIT_RX_IND:
            return new ZigBeeExplicitRxIndicator();
        case API_ZB_TRANSMIT_STATUS:
            return new ZigBeeTransmitStatus();
        case API_ZB_REGIST_JOINING_DEV_STAT:
            return new ZigBeeRegisterJoiningDeviceStatus();
        case API_ZB_DEV_AUTH_IND:
            return new ZigBeeDeviceAuthenticatedIndicator();
        case API_MOR_ROUTE_REQUEST_IND:
            return new ManyToOneRouteRequestIndicator();
        case API_ROUTE_REC_IND:
            return new RouteRecordIndicator();
        case API_RX_PACKET_IP_V4:
            return new ReceivePacketIPv4();
        case API_ROUTE_INFO_PACKET:
            return new RouteInformationPacket();
        case API_AGGREGATE_ADDR_UPDATE:
            return new AggregateAddressingUpdate();
        }
        return null;
    }
}
