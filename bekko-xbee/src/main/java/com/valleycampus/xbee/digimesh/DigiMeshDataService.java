/*
 * Copyright (C) 2013 Valley Campus Japan, Inc.
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
package com.valleycampus.xbee.digimesh;

import com.valleycampus.xbee.api.XBeeAPI;
import com.valleycampus.xbee.api.XBeeAPIListener;
import com.valleycampus.xbee.api.XBeeResponse;
import com.valleycampus.xbee.api.s2.ExplicitAddressingZigBeeCommandFrame;
import com.valleycampus.xbee.api.s2.XBeeAddressingIndicator;
import com.valleycampus.xbee.api.s2.XBeeAddressingRequest;
import com.valleycampus.xbee.api.s2.ZigBeeExplicitRxIndicator;
import com.valleycampus.xbee.api.s2.ZigBeeReceivePacket;
import com.valleycampus.xbee.api.s2.ZigBeeTransmitStatus;
import com.valleycampus.zigbee.IEEEAddress;
import com.valleycampus.zigbee.NetworkAddress;
import com.valleycampus.zigbee.ZigBeeAddress;
import com.valleycampus.zigbee.ZigBeeConst;
import com.valleycampus.zigbee.ZigBeeDataConnection;
import com.valleycampus.zigbee.aps.DataIndication;
import com.valleycampus.zigbee.aps.DataReceiver;
import com.valleycampus.zigbee.aps.DataRequest;
import com.valleycampus.zigbee.aps.DataService;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import com.valleycampus.zigbee.io.ByteUtil;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class DigiMeshDataService implements XBeeAPIListener, DataService {

    public static final int DEFAULT_TIMEOUT = 10000;
    public static final short DIGI_PROFILE_ID = (short) 0xC105;
    public static final short DIGI_CLUSTER_ID = (short) 0x0011;
    public static final int DIGI_ENDPOINT = 0xE8;

    private XBeeAPI xbAPI;
    private List listenerList;
    
    public DigiMeshDataService(XBeeAPI xbDevice) {
        this.xbAPI = xbDevice;
        this.listenerList = new Vector();
        xbDevice.addAPIListener(this);
    }
    
    public byte transmit(DataRequest request) throws IOException {
        ExplicitAddressingZigBeeCommandFrame tx = assembleFrame(request);
        ZigBeeTransmitStatus resp = (ZigBeeTransmitStatus) xbAPI.syncSubmit(tx, DEFAULT_TIMEOUT);
        byte status = resp.getDeliveryStatus();
        if (ZigBeeTransmitStatus.STATUS_SUCCESS != status) {
            throw new IOException("XBee Delivery Status (0x" + ByteUtil.toHexString(status) + ")");
        }
        return ZigBeeConst.SUCCESS;
    }
    
    private ExplicitAddressingZigBeeCommandFrame assembleFrame(DataRequest request) throws IOException {
        ExplicitAddressingZigBeeCommandFrame tx = new ExplicitAddressingZigBeeCommandFrame();
        ZigBeeAddress address = request.getDestinationAddress();
        if (address instanceof IEEEAddress) {
            IEEEAddress eui64 = (IEEEAddress) address;
            tx.setAddress16(XBeeAddressingRequest.XBEE_BROADCAST16);
            tx.setAddress64(eui64.toLong());
        } else {
            throw new UnsupportedOperationException("Unsupported addressing mode.");
        }
        tx.setProfileId(request.getProfileId());
        tx.setClusterId(request.getClusterId());
        tx.setSourceEndpoint((byte) request.getSourceEndpoint());
        tx.setDestinationEndpoint((byte) request.getDestinationEndpoint());
        tx.setPayload(request.getPayload());
        int option = 0x00;
        if ((request.getTxOptions() & ZigBeeDataConnection.OPTION_ACK) == 0) {
            option |= 0x01;
        }
        tx.setTransmitOptions((byte) option);
        tx.setBroadcastRadius((byte) request.getRadius());
        return tx;
    }

    public void addDataReceiver(DataReceiver receiver) {
        listenerList.add(receiver);
    }
    
    public void removeDataReceiver(DataReceiver receiver) {
        listenerList.remove(receiver);
    }
    
    public void handleResponse(XBeeResponse response) {
        if (response instanceof XBeeAddressingIndicator) {
            XBeeAddressingIndicator indicator = (XBeeAddressingIndicator) response;
            IEEEAddress eui64 = IEEEAddress.getByAddress(indicator.getAddress64());
            NetworkAddress nwk = NetworkAddress.getByAddress(indicator.getAddress16());
            if ((indicator instanceof ZigBeeExplicitRxIndicator) || (indicator instanceof ZigBeeReceivePacket)) {
                DataIndication dataIndication = new DataIndication();
                if (indicator.getAddress64() != XBeeAddressingIndicator.XBEE_UNKNOWN64) {
                    dataIndication.setSourceAddress(eui64);
                } else {
                    dataIndication.setSourceAddress(nwk);
                }
                if (indicator instanceof ZigBeeExplicitRxIndicator) {
                    ZigBeeExplicitRxIndicator rx = (ZigBeeExplicitRxIndicator) indicator;
                    dataIndication.setClusterId(rx.getClusterId());
                    dataIndication.setProfileId(rx.getProfileId());
                    dataIndication.setSourceEndpoint(rx.getSourceEndpoint());
                    dataIndication.setDestinationEndpoint(rx.getDestinationEndpoint());
                    dataIndication.setPayload(rx.getPayload());
                } else {
                    ZigBeeReceivePacket rx = (ZigBeeReceivePacket) indicator;
                    dataIndication.setClusterId(DIGI_CLUSTER_ID);
                    dataIndication.setProfileId(DIGI_PROFILE_ID);
                    dataIndication.setSourceEndpoint(DIGI_ENDPOINT);
                    dataIndication.setDestinationEndpoint(DIGI_ENDPOINT);
                    dataIndication.setPayload(rx.getPacket());
                }
                // TODO
                //dataIndication.setLinkQuality(linkQuality);
                dataIndication.setSecurityStatus(ZigBeeConst.UNSECURED);    // FIXME
                for (int i = 0; i < listenerList.size(); i++) {
                    ((DataReceiver) listenerList.get(i)).received(dataIndication);
                }
            }
        }
    }

    public int getMaxAPDUSize() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
