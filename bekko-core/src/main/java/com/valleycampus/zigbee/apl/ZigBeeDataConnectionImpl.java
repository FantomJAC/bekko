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
package com.valleycampus.zigbee.apl;

import com.valleycampus.zigbee.ZigBeeConst;
import com.valleycampus.zigbee.ZigBeeDataConnection;
import com.valleycampus.zigbee.ZigBeeDataPacket;
import com.valleycampus.zigbee.ZigBeeSimpleDescriptor;
import com.valleycampus.zigbee.aps.DataIndication;
import com.valleycampus.zigbee.aps.DataReceiver;
import com.valleycampus.zigbee.aps.DataRequest;
import com.valleycampus.zigbee.aps.DataService;
import com.valleycampus.zigbee.zdo.ZigBeeDevice;
import java.io.IOException;
import com.valleycampus.zigbee.util.ArrayFifoQueue;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class ZigBeeDataConnectionImpl implements ZigBeeDataConnection, DataReceiver  {

    public static final int DEFAULT_RECEIVE_QUEUE_SIZE = 10;
    private ZigBeeSimpleDescriptor simpleDescriptor;
    private DataService dataService;
    private ZigBeeDevice zdo;
    private ArrayFifoQueue receiveQueue;
    
    public ZigBeeDataConnectionImpl() {
        this.receiveQueue = new ArrayFifoQueue(DEFAULT_RECEIVE_QUEUE_SIZE);
    }
    
    public ZigBeeSimpleDescriptor getSimpleDescriptor() {
        return simpleDescriptor;
    }

    protected void open(ZigBeeDevice zdo, ZigBeeSimpleDescriptor simpleDescriptor) throws IOException {
        this.zdo = zdo;
        this.dataService = zdo.getDataService();
        dataService.addDataReceiver(this);
        int actualEndpoint = zdo.addEndpoint(simpleDescriptor);
        this.simpleDescriptor = zdo.getSimpleDescriptor(actualEndpoint);
    }

    public void close() throws IOException {
        zdo.removeEndpoint(simpleDescriptor.getEndpoint());
        dataService.removeDataReceiver(this);
    }

    public ZigBeeDataPacket createZigBeeDataPacket() {
        return new ZigBeeDataPacketImpl();
    }

    public void send(ZigBeeDataPacket dataPacket, int radius, int txOptions) throws IOException {
        DataRequest request = toDataRequest(dataPacket);
        request.setTxOptions(txOptions);
        
        if (dataPacket.getLocalEndpoint() == ZigBeeConst.BRODCAST_ENDPOINT) {
            request.setSourceEndpoint((byte) simpleDescriptor.getEndpoint());
        } else {
            // Force using local endpoint.
            request.setSourceEndpoint((byte) dataPacket.getLocalEndpoint());
        }
        
        request.setProfileId(simpleDescriptor.getApplicationProfileIdentifier());
        request.setRadius(radius);
        byte status = dataService.transmit(request);
        dataPacket.setStatus(status);
    }

    public void receive(ZigBeeDataPacket dataPacket, int timeout) throws IOException {
        DataIndication indication = (DataIndication) receiveQueue.blockingDequeue(timeout);
        if (indication == null) {
            throw new IOException("Timeout or Invalid");
        }
        
        dataPacket.setAddress(indication.getSourceAddress());
        dataPacket.setData(indication.getPayload(), indication.getOffset(), indication.getLength());
        dataPacket.setEndpoint(indication.getSourceEndpoint());
        dataPacket.setLocalAddress(indication.getDestinationAddress());
        dataPacket.setLocalEndpoint(indication.getDestinationEndpoint());
        dataPacket.setClusterId(indication.getClusterId());
        dataPacket.setStatus(indication.getStatus());
        dataPacket.setSecurityStatus(indication.getSecurityStatus());
        dataPacket.setLinkQuality(indication.getLinkQuality());
        // TODO: RXTime
    }

    public int getMaxSinglePayloadLength() {
        try {
            return dataService.getMaxAPDUSize();
        } catch (IOException ex) {
            throw new IllegalStateException("Can't read max apdu size. " + ex);
        }
    }

    public void received(DataIndication indication) {
        if (indication.getProfileId() != simpleDescriptor.getApplicationProfileIdentifier()) {
            // Ignore incompatible profiles.
            return;
        }
        
        DataIndication copy = null;
        
        if (indication.getDestinationEndpoint() == ZigBeeConst.BRODCAST_ENDPOINT) {
            copy = copyDataIndication(indication);
            copy.setDestinationEndpoint(simpleDescriptor.getEndpoint());
        } else if (indication.getDestinationEndpoint() == simpleDescriptor.getEndpoint()) {
            copy = copyDataIndication(indication);
        }
        
        if (copy != null) {
            receiveQueue.enqueue(copy);
        }
    }
    
    private static DataRequest toDataRequest(ZigBeeDataPacket dataPacket) {
        DataRequest request = new DataRequest();

        request.setDestinationAddress(dataPacket.getAddress());
        request.setDestinationEndpoint(dataPacket.getEndpoint());
        request.setClusterId(dataPacket.getClusterId());
        request.setPayload(dataPacket.getData(), dataPacket.getOffset(), dataPacket.getLength());
        
        return request;
    }
    
    /*
     * Create a shallow copy.
     */
    private static DataIndication copyDataIndication(DataIndication di) {
        DataIndication copy = new DataIndication();
        copy.setDestinationAddress(di.getDestinationAddress());
        copy.setDestinationEndpoint(di.getDestinationEndpoint());
        copy.setSourceAddress(di.getSourceAddress());
        copy.setSourceEndpoint(di.getSourceEndpoint());
        copy.setProfileId(di.getProfileId());
        copy.setClusterId(di.getClusterId());
        copy.setStatus(di.getStatus());
        copy.setSecurityStatus(di.getSecurityStatus());
        copy.setLinkQuality(di.getLinkQuality());
        copy.setPayload(di.getPayload(), di.getOffset(), di.getLength());
        return copy;
    }
}
