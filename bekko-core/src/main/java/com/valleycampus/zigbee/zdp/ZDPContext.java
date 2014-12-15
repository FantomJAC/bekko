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
package com.valleycampus.zigbee.zdp;

import com.valleycampus.zigbee.ZigBeeAddress;
import com.valleycampus.zigbee.ZigBeeConst;
import com.valleycampus.zigbee.ZigBeeDataConnection;
import com.valleycampus.zigbee.ZigBeeException;
import com.valleycampus.zigbee.aps.DataIndication;
import com.valleycampus.zigbee.aps.DataReceiver;
import com.valleycampus.zigbee.aps.DataRequest;
import com.valleycampus.zigbee.aps.DataService;
import com.valleycampus.zigbee.zdo.ZDPCommandPacket;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import com.valleycampus.zigbee.io.ByteUtil;
import com.valleycampus.zigbee.io.Frame;
import com.valleycampus.zigbee.io.FrameBuffer;
import com.valleycampus.zigbee.util.Sequence;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public final class ZDPContext implements DataReceiver {

    public static final int DEFAULT_TX_OPTIONS =
            ZigBeeDataConnection.OPTION_ACK |
            ZigBeeDataConnection.OPTION_FRAGMENTATION;
    //      DataRequest.EXT_OPTION_SOURCE_EUI64;
    private DataService dataService;
    private ArrayList serverList;
    private Sequence transactionSequence;
    private LinkedList transactionList;
    private static PrintStream logStream = System.out;
    private static volatile boolean debugEnabled;
    
    public ZDPContext(DataService dataService) {
        this.dataService = dataService;
        serverList = new ArrayList();
        transactionSequence = new Sequence(8);
        transactionList = new LinkedList();
        dataService.addDataReceiver(this);
    }
    
    public static void setLogStream(PrintStream logStream) {
        ZDPContext.logStream = logStream;
    }
    
    public static void setDebugEnabled(boolean debugEnabled) {
        ZDPContext.debugEnabled = debugEnabled;
    }
    
    protected static void debug(String s) {
        if (debugEnabled) {
            logStream.println("[ZDP#debug] " + s);
        }
    }
    
    protected static void warn(String s) {
        logStream.println("[ZDP#warn] " + s);
    }
    
    protected static void debug(ZDPCommandPacket zdpPacket, boolean resp) {
        if (debugEnabled) {
            StringBuffer sb = new StringBuffer();
            sb.append("[").append(ByteUtil.toHexString(zdpPacket.getClusterId())).append("] ");
            if (resp) {
                sb.append(zdpPacket.getRemoteAddress().toString()).append(" ")
                .append(" >>(").append(zdpPacket.getPayloadLength()).append(") ");
            } else {
                sb.append(zdpPacket.getRemoteAddress().toString()).append(" ")
                .append(" <<(").append(zdpPacket.getPayloadLength()).append(") ");
            }
            byte[] buffer = zdpPacket.getPayload();
            for (int i = zdpPacket.getPayloadOffset(); i < zdpPacket.getPayloadOffset() + zdpPacket.getPayloadLength(); i++) {
                sb.append(ByteUtil.toHexString(buffer[i])).append(" ");
            }
            logStream.println("[ZDP#debug] " + sb.toString());
        }
    }
    
    protected void registerZDPServerService(ZDPService service) {
        serverList.add(service);
    }

    private void sendCommandPacket(ZDPCommandPacket packet, int txOptions, int radius) throws IOException {
        DataRequest dataRequest = new DataRequest();
        
        FrameBuffer frameBuffer = new FrameBuffer(FrameBuffer.BO_LITTLE_ENDIAN, new byte[packet.quote()]);
        packet.pull(frameBuffer);
        dataRequest.setPayload(frameBuffer.getRawArray());
        
        dataRequest.setDestinationAddress(packet.getRemoteAddress());
        dataRequest.setDestinationEndpoint(ZigBeeConst.ZDO_ENDPOINT);
        dataRequest.setClusterId(packet.getClusterId());
        dataRequest.setSourceEndpoint(ZigBeeConst.ZDO_ENDPOINT);
        dataRequest.setProfileId(ZigBeeConst.PROFILE_ZDP);
        dataRequest.setTxOptions(txOptions);
        dataRequest.setRadius(radius);
        debug(packet, false);
        byte status = dataService.transmit(dataRequest);
        if (status != ZigBeeConst.SUCCESS) {
            throw new ZigBeeException(status);
        }
    }
    
    protected int sendCommand(ZigBeeAddress address, short clusterId, Frame command, int txOptions, int radius, int tsn) throws IOException {
        ZDPCommandPacket reqPacket = new ZDPCommandPacket();
        reqPacket.fillPayload(command);
        reqPacket.setRemoteAddress(address);
        reqPacket.setClusterId(clusterId);
        reqPacket.setTsn((tsn != -1) ? tsn : transactionSequence.nextSequence());
        sendCommandPacket(reqPacket, txOptions, radius);
        return reqPacket.getTsn();
    }
    
    protected ZDPCommandPacket sendCommandSync(ZigBeeAddress address, short clusterId, Frame command, int txOptions, int radius, long timeout) throws IOException {
        Transaction t = nextTransaction();
        synchronized (t) {
            ZDPCommandPacket result = null;
            int tsn = sendCommand(address, clusterId, command, txOptions, radius, -1);
            t.tsn = tsn;
            t.clusterId = clusterId;
            t.active = true;
            long start = System.currentTimeMillis();
            long elapsed = 0;
            do {
                try {
                    t.wait(timeout - elapsed);
                } catch (InterruptedException ignore) {
                }
                if (t.result != null) {
                    result = t.result;
                    break;
                }
                elapsed = System.currentTimeMillis() - start;
            } while (elapsed < timeout);
            t.active = false;
            return result;
        }
    }
    
    public void received(DataIndication indication) {
        if (indication.getDestinationEndpoint()!= ZigBeeConst.ZDO_ENDPOINT ||
            indication.getProfileId() != ZigBeeConst.PROFILE_ZDP) {
            return;
        }
        
        // Generate packet.
        ZDPCommandPacket packet = new ZDPCommandPacket();
        packet.drain(new FrameBuffer(
                FrameBuffer.BO_LITTLE_ENDIAN,
                indication.getPayload(), indication.getOffset(), indication.getLength()));
        packet.setRemoteAddress(indication.getSourceAddress());
        packet.setLocalAddress(indication.getDestinationAddress());
        packet.setClusterId(indication.getClusterId());
        
        debug(packet, true);

        for (int i = 0; i < serverList.size(); i++) {
            ZDPService service = (ZDPService) serverList.get(i);
            if (service.handleZDPCommand(packet)) {
                debug("Handled by ServerZDPService cid=0x" + ByteUtil.toHexString(packet.getClusterId()));
                break;
            }
        }
        
        if (handleZDPCommandResponse(packet)) {
            debug("Handled by ClientZDPService cid=0x" + ByteUtil.toHexString(packet.getClusterId()));
        }
    }
    
    private synchronized Transaction nextTransaction() {
        Transaction t = new Transaction();
        transactionList.add(t);
        return t;
    }

    private synchronized boolean handleZDPCommandResponse(ZDPCommandPacket zdpCommand) {
        for (int i = 0; i < transactionList.size(); i++) {
            Transaction t = (Transaction) transactionList.get(i);
            synchronized (t) {
                if (t.active && t.match(zdpCommand)) {
                    transactionList.remove(i);
                    t.result = zdpCommand;
                    t.notify();
                    return true;
                }
            }
        }
        return false;
    }
    
    private static class Transaction {
        
        private int tsn;
        private short clusterId;
        private boolean active;
        private ZDPCommandPacket result = null;
        
        public boolean match(ZDPCommandPacket zdpCommand) {
            int cidResp = (zdpCommand.getClusterId() ^ 0x8000) & 0xFFFF;
            return this.tsn == zdpCommand.getTsn() &&
                   (this.clusterId & 0xFFFF) == cidResp;
        }
    }
}
