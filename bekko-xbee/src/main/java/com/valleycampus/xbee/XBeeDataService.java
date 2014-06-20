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
package com.valleycampus.xbee;

import com.valleycampus.xbee.api.XBeeAPIListener;
import com.valleycampus.xbee.api.XBeeResponse;
import com.valleycampus.xbee.api.XBeeAPI;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import com.valleycampus.zigbee.aps.DataIndication;
import com.valleycampus.zigbee.aps.DataReceiver;
import com.valleycampus.zigbee.aps.DataRequest;
import com.valleycampus.xbee.api.s2.ExplicitAddressingZigBeeCommandFrame;
import com.valleycampus.xbee.api.s2.XBeeAddressingIndicator;
import com.valleycampus.xbee.api.s2.XBeeAddressingRequest;
import com.valleycampus.xbee.api.s2.ZigBeeExplicitRxIndicator;
import com.valleycampus.xbee.api.s2.ZigBeeReceivePacket;
import com.valleycampus.xbee.api.s2.ZigBeeTransmitStatus;
import com.valleycampus.zigbee.GroupAddress;
import com.valleycampus.zigbee.IEEEAddress;
import com.valleycampus.zigbee.NetworkAddress;
import com.valleycampus.zigbee.ZigBeeAddress;
import com.valleycampus.zigbee.ZigBeeConst;
import com.valleycampus.zigbee.ZigBeeDataConnection;
import com.valleycampus.zigbee.aps.DataService;
import com.valleycampus.zigbee.io.ByteUtil;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class XBeeDataService implements XBeeAPIListener, DataService {

    public static final int DEFAULT_TIMEOUT = 10000;
    public static final short DIGI_PROFILE_ID = (short) 0xC105;
    public static final short DIGI_CLUSTER_ID = (short) 0x0011;
    public static final int DIGI_ENDPOINT = 0xE8;
    
    private XBeeNetworkManager nwkMgr;
    private XBeeBindingManager bindingMgr;
    private XBeeAPI xbAPI;
    private List listenerList;
    
    public XBeeDataService(XBeeNetworkManager nwkMgr, XBeeBindingManager bindingMgr, XBeeAPI xbDevice) {
        this.nwkMgr = nwkMgr;
        this.bindingMgr = bindingMgr;
        this.xbAPI = xbDevice;
        this.listenerList = new Vector();
        xbDevice.addAPIListener(this);
    }
    
    public byte transmit(DataRequest request) throws IOException {
        if (request.getDestinationAddress() == null) {
            List bindInfos = bindingMgr.getBindedInfos(request.getClusterId(), request.getSourceEndpoint());
            if (bindInfos.isEmpty()) {
                return ZigBeeConst.NO_BOUND_DEVICE;
            }
            for (int i = 0; i < bindInfos.size(); i++) {
                BindInfo bindInfo = (BindInfo) bindInfos.get(i);
                ZigBeeAddress address = bindInfo.getDestinationAddress();
                request.setDestinationAddress(address);
                ExplicitAddressingZigBeeCommandFrame tx = assembleFrame(request);
                tx.setDestinationEndpoint((byte) bindInfo.getDestinationEndpoint());
                transmit(tx);
            }
            return ZigBeeConst.SUCCESS;
        } else {
            ExplicitAddressingZigBeeCommandFrame tx = assembleFrame(request);
            tx.setDestinationEndpoint((byte) request.getDestinationEndpoint());
            transmit(tx);
            return ZigBeeConst.SUCCESS;
        }
    }
    
    private void transmit(ExplicitAddressingZigBeeCommandFrame tx) throws IOException {
        ZigBeeTransmitStatus resp = (ZigBeeTransmitStatus) xbAPI.syncSubmit(tx, DEFAULT_TIMEOUT);
        byte status = resp.getDeliveryStatus();
        if (ZigBeeTransmitStatus.STATUS_SUCCESS != status) {
            throw new IOException("XBee Delivery Status (0x" + ByteUtil.toHexString(status) + ")");
        }
        if (tx.getAddress64() != XBeeAddressingRequest.XBEE_BROADCAST64 &&
             tx.getAddress16() == XBeeAddressingRequest.XBEE_BROADCAST16) {
            // Keep a resolved address to the local address table.
            nwkMgr.updateAddressMap(
                    IEEEAddress.getByAddress(tx.getAddress64()),
                    NetworkAddress.getByAddress(resp.getAddress16()),
                    false);
        }
    }
    
    private ExplicitAddressingZigBeeCommandFrame assembleFrame(DataRequest request) throws IOException {
        ExplicitAddressingZigBeeCommandFrame tx = new ExplicitAddressingZigBeeCommandFrame();
        ZigBeeAddress address = request.getDestinationAddress();
        if (address instanceof IEEEAddress) {
            IEEEAddress eui64 = (IEEEAddress) address;
            NetworkAddress nwk = nwkMgr.lookupNodeIdByEui64(eui64);
            if (nwk == null) {
                // XBee may have the NWK in it's own address table.
                tx.setAddress16(XBeeAddressingRequest.XBEE_BROADCAST16);
            } else {
                tx.setAddress16(nwk.toShort());
            }
            tx.setAddress64(eui64.toLong());
        } else if (address instanceof GroupAddress) {
            throw new UnsupportedOperationException("XBee doesn't support group addressing.");
        } else {
            NetworkAddress nwk = (NetworkAddress) address;
            if (nwk.isBroadcast()) {
                tx.setAddress64(XBeeAddressingRequest.XBEE_BROADCAST64);
            } else {
                // For XBee, we must provide EUI64 when send unicast.
                IEEEAddress eui64 = nwkMgr.lookupEui64ByNodeId(nwk);
                if (eui64 == null) {
                    throw new IOException("Can't resolve NWK. (EUI64 not found in local address table.)");
                }
                tx.setAddress64(eui64.toLong());
            }
            tx.setAddress16(nwk.toShort());
        }
        tx.setProfileId(request.getProfileId());
        tx.setClusterId(request.getClusterId());
        tx.setSourceEndpoint((byte) request.getSourceEndpoint());
        tx.setPayload(request.getPayload());
        int option = 0x40;  // Use indirect transmission timeout.
        if ((request.getTxOptions() & ZigBeeDataConnection.OPTION_SECURITY) > 0) {
            option |= 0x20;
        }
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
            // Keep updated the local address table.
            nwkMgr.updateAddressMap(eui64, nwk, false);
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
