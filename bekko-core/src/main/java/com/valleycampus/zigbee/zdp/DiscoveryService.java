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

import com.valleycampus.zigbee.GroupAddress;
import com.valleycampus.zigbee.NetworkAddress;
import com.valleycampus.zigbee.ZigBeeSimpleDescriptor;
import com.valleycampus.zigbee.zdo.DiscoveryListener;
import com.valleycampus.zigbee.zdp.command.DeviceAnnounce;
import com.valleycampus.zigbee.zdp.command.MatchDescReq;
import com.valleycampus.zigbee.zdp.command.MatchDescRsp;
import com.valleycampus.zigbee.zdo.ZDPCommandPacket;
import com.valleycampus.zigbee.zdp.command.ActiveEpRsp;
import com.valleycampus.zigbee.zdp.command.DescReq;
import com.valleycampus.zigbee.zdp.command.SimpleDescReq;
import com.valleycampus.zigbee.zdp.command.SimpleDescRsp;
import com.valleycampus.zigbee.zdp.command.SimpleDescriptorFrame;
import com.valleycampus.zigbee.zdp.command.ZDPCommand;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.valleycampus.zigbee.service.Service;
import com.valleycampus.zigbee.service.ServiceTask;
import com.valleycampus.zigbee.util.ArrayFifoQueue;
import com.valleycampus.zigbee.zdo.NetworkManager;
import com.valleycampus.zigbee.zdo.ZigBeeDevice;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class DiscoveryService implements ZDPService, Service {
    
    public static final int DISPATCHER_TIMEOUT = 30000;
    public static final int DISPATCHER_QUEUE_SIZE = 32;
    private final ZigBeeDevice zdo;
    private final NetworkManager nwkMgr;
    private final DiscoveryDispatcher dispatcher;
    private final Object listenerLock = new Object();
    private final ArrayList listenerList = new ArrayList();
    
    protected DiscoveryService(ZigBeeDevice zdo, NetworkManager nwkMgr, ZDPContext zdpContext) {
        this.zdo = zdo;
        this.nwkMgr = nwkMgr;
        dispatcher = new DiscoveryDispatcher(zdpContext);
    }

    public boolean activate() {
        return dispatcher.activate();
    }

    public boolean shutdown() {
        return dispatcher.shutdown();
    }
    
    public void addDiscoveryListener(DiscoveryListener listener) {
        synchronized (listenerLock) {
            listenerList.add(listener);
        }
    }
    
    public boolean removeDiscoveryListener(DiscoveryListener listener) {
        synchronized (listenerLock) {
            return listenerList.remove(listener);
        }
    }
    
    /**
     * Match procedure described in ZigBee Specification 2.4.4.1.7.1
     * @param matchDescReq
     * @param simpleDescriptor
     * @return 
     */
    protected static boolean isMatch(MatchDescReq matchDescReq, ZigBeeSimpleDescriptor simpleDescriptor) {
        if (matchDescReq.getProfileID() != simpleDescriptor.getApplicationProfileIdentifier()) {
            // ProfileID dose not match.
            return false;
        }
        int matchNumIn = matchDescReq.getInClusterList().length;
        int matchNumOut = matchDescReq.getOutClusterList().length;
        if (matchNumIn == 0 && matchNumOut == 0) {
            // No Input/Output clusters are provided.
            // As described in 2.5.2.2, in this case we consider as matched.
            return true;
        }
        
        if (matchNumIn != 0) {
            int appNumIn = simpleDescriptor.getApplicationInputClusterCount() & 0xFF;
            for (int m = 0; m < matchNumIn; m++) {
                short matchCluster = matchDescReq.getInClusterList()[m];
                for (int a = 0; a < appNumIn; a++) {
                    short appCluster = simpleDescriptor.getApplicationInputClusterList()[a];
                    if (matchCluster == appCluster) {
                        return true;
                    }
                }
            }
        }
        if (matchNumOut != 0) {
            int appNumOut = simpleDescriptor.getApplicationOutputClusterCount() & 0xFF;
            for (int m = 0; m < matchNumOut; m++) {
                short matchCluster = matchDescReq.getOutClusterList()[m];
                for (int a = 0; a < appNumOut; a++) {
                    short appCluster = simpleDescriptor.getApplicationOutputClusterList()[a];
                    if (matchCluster == appCluster) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    public boolean handleZDPCommand(ZDPCommandPacket zdpCommand) {
        if (zdpCommand.getRemoteAddress() instanceof GroupAddress) {
            // Only Unicast and Broadcast commands are accepted.
            return false;
        }
        switch (zdpCommand.getClusterId()) {
        case ZDPCommand.ZDP_SIMPLE_DESC_REQ:
        case ZDPCommand.ZDP_ACTIVE_EP_REQ:
        case ZDPCommand.ZDP_MATCH_DESC_REQ:
            dispatcher.dispatchCommand(zdpCommand);
            return true;
        case ZDPCommand.ZDP_DEVICE_ANNCE:
            DeviceAnnounce deviceAnnounce = new DeviceAnnounce();
            deviceAnnounce.drain(ZDPCommandPacket.toFrameBuffer(zdpCommand));
            synchronized (listenerLock) {
                // We actually indicate to listeners 'synchronous'
                // All listeners should NOT block at the methods.
                for (int i = 0; i < listenerList.size(); i++) {
                    ((DiscoveryListener) listenerList.get(i)).deviceAnnounce(
                            deviceAnnounce.getAddr16(),
                            deviceAnnounce.getAddr64(),
                            deviceAnnounce.getCapability());
                }
            }
            return true;
        case ZDPCommand.ZDP_MATCH_DESC_RSP:
            MatchDescRsp matchDescRsp = new MatchDescRsp();
            matchDescRsp.drain(ZDPCommandPacket.toFrameBuffer(zdpCommand));
            final NetworkAddress[] matchList = new NetworkAddress[matchDescRsp.getMatchLength()];
            for (int i = 0; i < matchList.length; i++) {
                matchList[i] = NetworkAddress.getByAddress(
                        (short) matchDescRsp.getMatchList()[i]);
            }
            synchronized (listenerLock) {
                // We actually indicate to listeners 'synchronous'
                // All listeners should NOT block at the methods.
                for (int i = 0; i < listenerList.size(); i++) {
                    ((DiscoveryListener) listenerList.get(i)).deviceMatched(
                            zdpCommand.getTsn(),
                            zdpCommand.getRemoteAddress(),
                            matchList);
                }
            }
            return true;
        }
        return false;
    }
    
    private SimpleDescRsp doSimpleDescResponse(SimpleDescReq simpleDescReq) throws IOException {
        NetworkAddress nwkAddr = simpleDescReq.getNetworkAddr();
        int localNodeType = nwkMgr.getNodeType();
        NetworkAddress localAddr = nwkMgr.getNetworkAddress();
        
        SimpleDescRsp simpleDescRsp = new SimpleDescRsp();
        simpleDescRsp.setNetworkAddr(nwkAddr);
        
        int endpoint = simpleDescReq.getEndpoint();
        if (endpoint < 1 || 254 < endpoint) {
            simpleDescRsp.setStatus(ZDPCommand.STATUS_INVALID_EP);
            return simpleDescRsp;
        }
        
        if (nwkAddr.equals(localAddr)) {
            ZigBeeSimpleDescriptor simpleDescriptor = zdo.getSimpleDescriptor(endpoint);
            if (simpleDescriptor != null) {
                simpleDescRsp.setStatus(ZDPCommand.STATUS_SUCCESS);
                simpleDescRsp.setSimpleDescriptor(new SimpleDescriptorFrame(simpleDescriptor));
            } else {
                simpleDescRsp.setStatus(ZDPCommand.STATUS_NOT_ACTIVE);
            }
            return simpleDescRsp;
        }
        
        if (!nwkAddr.equals(localAddr)) {
            switch (localNodeType) {
            case NetworkManager.TYPE_END_DEVICE: {
                // EndDevice cannot have children.
                simpleDescRsp.setStatus(ZDPCommand.STATUS_INV_REQUESTTYPE);
                return simpleDescRsp;
            }
            case NetworkManager.TYPE_COORDINATOR:
            case NetworkManager.TYPE_ROUTER: {
                // TODO: Search for children...
                boolean deviceNotFound = true;
                if (deviceNotFound) {
                    simpleDescRsp.setStatus(ZDPCommand.STATUS_DEVICE_NOT_FOUND);
                    return simpleDescRsp;
                }
            }
            default:
                throw new IllegalStateException("Unknown NodeType");
            }
        }
        
        return null;
    }
    
    private ActiveEpRsp doActiveEpResponse(DescReq activeEpReq) throws IOException {
        NetworkAddress nwkAddr = activeEpReq.getNetworkAddr();
        int localNodeType = nwkMgr.getNodeType();
        NetworkAddress localAddr = nwkMgr.getNetworkAddress();
        
        ActiveEpRsp activeEpRsp = new ActiveEpRsp();
        activeEpRsp.setNetworkAddr(nwkAddr);
        
        if (nwkAddr.equals(localAddr)) {
            activeEpRsp.setStatus(ZDPCommand.STATUS_SUCCESS);
            activeEpRsp.setActiveEpList(zdo.getActiveEndpoints());
            return activeEpRsp;
        }
        
        if (!nwkAddr.equals(localAddr)) {
            switch (localNodeType) {
            case NetworkManager.TYPE_END_DEVICE: {
                // EndDevice cannot have children.
                activeEpRsp.setStatus(ZDPCommand.STATUS_INV_REQUESTTYPE);
                return activeEpRsp;
            }
            case NetworkManager.TYPE_COORDINATOR:
            case NetworkManager.TYPE_ROUTER: {
                // TODO: Search for children...
                boolean deviceNotFound = true;
                if (deviceNotFound) {
                    activeEpRsp.setStatus(ZDPCommand.STATUS_DEVICE_NOT_FOUND);
                    return activeEpRsp;
                }
            }
            default:
                throw new IllegalStateException("Unknown NodeType");
            }
        }
        
        return null;
    }
    
    private int isMatch(MatchDescReq matchDescReq, int[] endpoints, int[] matchList) {
        int matchLength = 0;
        for (int i = 0; i < endpoints.length; i++) {
            int endpoint = endpoints[i];
            if (isMatch(matchDescReq, zdo.getSimpleDescriptor(endpoint))) {
                matchList[matchLength++] = endpoint;
            }
        }
        return matchLength;
    }

    private List doMatchDescResponse(MatchDescReq matchDescReq) throws Exception {
        ArrayList matchedList = new ArrayList();
        
        NetworkAddress nwkAddr = matchDescReq.getNetworkAddr();
        int localNodeType = nwkMgr.getNodeType();
        NetworkAddress localAddr = nwkMgr.getNetworkAddress();

        // Apply the match criterion to local simple descriptors,
        // if the NwkAddrOf interest equals to local address or is broadcast.
        if (nwkAddr.equals(localAddr) || nwkAddr.isBroadcast()) {
            int[] endpoints = zdo.getActiveEndpoints();
            int[] matchList = new int[endpoints.length];
            int matchLength = isMatch(matchDescReq, endpoints, matchList);
            MatchDescRsp match = new MatchDescRsp();
            match.setNetworkAddr(localAddr);
            match.setStatus(ZDPCommand.STATUS_SUCCESS);
            match.setMatchLength((byte) matchLength);
            match.setMatchList(matchList);
            matchedList.add(match);
        }
        
        // Apply the match criterion to children simple descriptors,
        if (!nwkAddr.equals(localAddr) || nwkAddr.isBroadcast()) {
            switch (localNodeType) {
            case NetworkManager.TYPE_END_DEVICE: {
                // EndDevice cannot have children.
                MatchDescRsp match = new MatchDescRsp();
                match.setNetworkAddr(nwkAddr);
                match.setStatus(ZDPCommand.STATUS_INV_REQUESTTYPE);
                match.setMatchLength((byte) 0);
                matchedList.add(match);
                break;
            }
            case NetworkManager.TYPE_COORDINATOR:
            case NetworkManager.TYPE_ROUTER: {
                // TODO: Search for children...
                boolean deviceNotFound = true;
                if (deviceNotFound && !nwkAddr.isBroadcast()) {
                    MatchDescRsp match = new MatchDescRsp();
                    match.setNetworkAddr(nwkAddr);
                    match.setStatus(ZDPCommand.STATUS_DEVICE_NOT_FOUND);
                    match.setMatchLength((byte) 0);
                    matchedList.add(match);
                }
                break;
            }
            default:
                throw new IllegalStateException("Unknown NodeType");
            }
        }
        
        return matchedList;
    }
    
    private class DiscoveryDispatcher extends ServiceTask {
        
        private final ArrayFifoQueue indicationQueue = new ArrayFifoQueue(DISPATCHER_QUEUE_SIZE);
        private final ZDPContext zdpContext;
        
        public DiscoveryDispatcher(ZDPContext zdpContext) {
            super("ZDP ZDODiscovery Dispatcher Thread");
            this.zdpContext = zdpContext;
        }
        
        protected void taskLoop() {
            Object command = indicationQueue.blockingDequeue(DISPATCHER_TIMEOUT);
            if (command == null) {
                return;
            }
            ZDPCommandPacket commandPacket = (ZDPCommandPacket) command;
            try {
                switch (commandPacket.getClusterId()) {
                case ZDPCommand.ZDP_SIMPLE_DESC_REQ:
                    SimpleDescReq simpleDescReq = new SimpleDescReq();
                    simpleDescReq.drain(ZDPCommandPacket.toFrameBuffer(commandPacket));
                    SimpleDescRsp simpleDescRsp = doSimpleDescResponse(simpleDescReq);
                    if (simpleDescRsp != null) {
                        zdpContext.sendCommand(
                                commandPacket.getRemoteAddress(),
                                ZDPCommand.ZDP_SIMPLE_DESC_RSP,
                                simpleDescRsp,
                                ZDPContext.DEFAULT_TX_OPTIONS, 0,
                                commandPacket.getTsn());
                    }
                case ZDPCommand.ZDP_ACTIVE_EP_REQ:
                    DescReq activeEpReq = new DescReq();
                    activeEpReq.drain(ZDPCommandPacket.toFrameBuffer(commandPacket));
                    ActiveEpRsp activeEpRsp = doActiveEpResponse(activeEpReq);
                    if (activeEpRsp != null) {
                        zdpContext.sendCommand(
                                commandPacket.getRemoteAddress(),
                                ZDPCommand.ZDP_ACTIVE_EP_RSP,
                                activeEpRsp,
                                ZDPContext.DEFAULT_TX_OPTIONS, 0,
                                commandPacket.getTsn());
                    }
                    break;
                case ZDPCommand.ZDP_MATCH_DESC_REQ:
                    MatchDescReq matchDescReq = new MatchDescReq();
                    matchDescReq.drain(ZDPCommandPacket.toFrameBuffer(commandPacket));
                    List matchedList = doMatchDescResponse(matchDescReq);
                    for (int i = 0; i < matchedList.size(); i++) {
                        MatchDescRsp matchDescRsp = (MatchDescRsp) matchedList.get(i);
                        if (matchDescRsp.getMatchLength() != 0 || commandPacket.getLocalAddress().isUnicast()) {
                            zdpContext.sendCommand(
                                    commandPacket.getRemoteAddress(),
                                    ZDPCommand.ZDP_MATCH_DESC_RSP,
                                    matchDescRsp,
                                    ZDPContext.DEFAULT_TX_OPTIONS, 0,
                                    commandPacket.getTsn());
                        }
                    }
                    break;
                }
            } catch (Exception ex) {
                ZDPContext.warn("Failed to response: " + ex);
            }
        }
        
        public void dispatchCommand(ZDPCommandPacket command) {
            if (!indicationQueue.enqueue(command)) {
                ZDPContext.warn("IndicationDispatcher queue is full!");
            }
        }
    }
}
