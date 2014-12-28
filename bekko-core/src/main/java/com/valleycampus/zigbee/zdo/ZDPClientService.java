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
package com.valleycampus.zigbee.zdo;

import com.valleycampus.zigbee.IEEEAddress;
import com.valleycampus.zigbee.NetworkAddress;
import com.valleycampus.zigbee.ZigBeeAddress;
import com.valleycampus.zigbee.ZigBeeDataConnection;
import com.valleycampus.zigbee.ZigBeeSimpleDescriptor;
import com.valleycampus.zigbee.zdp.command.ActiveEpRsp;
import com.valleycampus.zigbee.zdp.command.AddrReq;
import com.valleycampus.zigbee.zdp.command.AddrRsp;
import com.valleycampus.zigbee.zdp.command.BindReq;
import com.valleycampus.zigbee.zdp.command.DescReq;
import com.valleycampus.zigbee.zdp.command.EndDeviceBindReq;
import com.valleycampus.zigbee.zdp.command.MatchDescReq;
import com.valleycampus.zigbee.zdp.command.MatchDescRsp;
import com.valleycampus.zigbee.zdp.command.MgmtLeaveReq;
import com.valleycampus.zigbee.zdp.command.MgmtRsp;
import com.valleycampus.zigbee.zdp.command.NodeDescRsp;
import com.valleycampus.zigbee.zdp.command.SimpleDescReq;
import com.valleycampus.zigbee.zdp.command.SimpleDescRsp;
import com.valleycampus.zigbee.zdp.command.ZDPCommand;
import java.io.IOException;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class ZDPClientService {
    
    public static final int DEFAULT_TX_OPTIONS = ZigBeeDataConnection.OPTION_ACK | ZigBeeDataConnection.OPTION_FRAGMENTATION;
    public static final long DEFAULT_TIMEOUT = 7800;
    
    protected ZigBeeDevice zdo;
    protected volatile int txOptions = DEFAULT_TX_OPTIONS;
    protected volatile int radius = 0;
    protected volatile long timeout = DEFAULT_TIMEOUT;
    
    public ZDPClientService(ZigBeeDevice zdo) {
        this.zdo = zdo;
    }
 
    public void setTXOptions(int txOptions) {
        this.txOptions = txOptions;
    }
    
    public void setRadius(int radius) {
        this.radius = radius;
    }
    
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
    
    private void ensureUnicast(ZigBeeAddress address) throws IOException {
        if (!address.isUnicast()) {
            throw new IOException("Remote address must be unicast");
        }
    }
    
    protected ZDPCommandPacket sendZDPCommand(ZigBeeAddress address, short clusterId, ZDPCommand command) throws IOException {
        ZDPCommandPacket result = zdo.sendZDPCommand(address, clusterId, command, txOptions, radius, timeout);
        if (result == null) {
            throw new IOException("ZDP Client Timeout");
        }
        return result;
    }
    
    //
    // Binding Client Service
    //
    
    /**
     * ZDP End_Device_Bind_req
     * @param coordinator request destination.
     * @param bindingTarget null if binding target is local device, otherwise primary binding cache device.
     * @param appContext Application wishes to be unbound.
     * @return Confirmed status.
     */
    public byte zdpEndDeviceBindRequest(ZigBeeAddress coordinator,
                                        NetworkAddress bindingTarget,
                                        int endpoint)
                                 throws IOException {
        EndDeviceBindReq endDeviceBindReq = new EndDeviceBindReq();
        if (bindingTarget == null) {
            NetworkAddress local = zdo.getNetworkManager().getNetworkAddress();
            endDeviceBindReq.setBindingTarget(local);
        } else {
            endDeviceBindReq.setBindingTarget(bindingTarget);
        }
        endDeviceBindReq.setSrcIEEEAddress(zdo.getIEEEAddress());
        endDeviceBindReq.setSrcEndpoint(endpoint);
        ZigBeeSimpleDescriptor simpleDescriptor = zdo.getSimpleDescriptor(endpoint);
        endDeviceBindReq.setProfileId(simpleDescriptor.getApplicationProfileIdentifier());
        short[] inClusterList = new short[simpleDescriptor.getApplicationInputClusterCount()];
        System.arraycopy(simpleDescriptor.getApplicationInputClusterList(), 0, inClusterList, 0, inClusterList.length);
        endDeviceBindReq.setInClusterList(inClusterList);
        short[] outClusterList = new short[simpleDescriptor.getApplicationOutputClusterCount()];
        System.arraycopy(simpleDescriptor.getApplicationOutputClusterList(), 0, outClusterList, 0, outClusterList.length);
        endDeviceBindReq.setOutClusterList(outClusterList);

        ensureUnicast(coordinator);
        ZDPCommandPacket packet = sendZDPCommand(coordinator, ZDPCommand.ZDP_END_DEVICE_BIND_REQ, endDeviceBindReq);
        
        MgmtRsp rsp = new MgmtRsp();
        rsp.drain(ZDPCommandPacket.toFrameBuffer(packet));
        return rsp.getStatus();
    }

    /**
     * ZDP Bind_req
     * @param bindingTarget could be null if the binding target is the same as srcAddress, otherwise primary binding cache device.
     * @param srcAddress
     * @param srcEndpoint
     * @param clusterId
     * @param dstAddress
     * @param dstEndpoint
     * @return Confirmed status.
     */
    public byte zdpBindRequest(ZigBeeAddress bindingTarget,
                               IEEEAddress srcAddress,
                               int srcEndpoint,
                               short clusterId,
                               ZigBeeAddress dstAddress,
                               int dstEndpoint)
                        throws IOException {
        return zdpBindRequest(bindingTarget, srcAddress, srcEndpoint, clusterId, dstAddress, dstEndpoint, false);
    }

    /**
     * ZDP Unbind_req
     * @param bindingTarget could be null if the binding target is the same as srcAddress, otherwise primary binding cache device.
     * @param srcAddress
     * @param srcEndpoint
     * @param clusterId
     * @param dstAddress
     * @param dstEndpoint
     * @return Confirmed status.
     */
    public byte zdpUnbindRequest(ZigBeeAddress bindingTarget,
                                 IEEEAddress srcAddress,
                                 int srcEndpoint,
                                 short clusterId,
                                 ZigBeeAddress dstAddress,
                                 int dstEndpoint)
                          throws IOException {
        return zdpBindRequest(bindingTarget, srcAddress, srcEndpoint, clusterId, dstAddress, dstEndpoint, true);
    }
    
    private byte zdpBindRequest(ZigBeeAddress bindingTarget,
                               IEEEAddress srcAddress,
                               int srcEndpoint,
                               short clusterId,
                               ZigBeeAddress dstAddress,
                               int dstEndpoint,
                               boolean unbind)
                        throws IOException {
        short cid = unbind ? ZDPCommand.ZDP_UNBIND_REQ : ZDPCommand.ZDP_BIND_REQ;
        BindReq bindReq = new BindReq();
        bindReq.setSourceIEEEAddress(srcAddress);
        bindReq.setSourceEndpoint(srcEndpoint);
        bindReq.setClusterId(clusterId);
        bindReq.setDestinationAddress(dstAddress);
        bindReq.setDestinationEndpoint(dstEndpoint);
        
        if (bindingTarget == null) {
            bindingTarget = srcAddress;
        }
        
        ensureUnicast(bindingTarget);
        ZDPCommandPacket packet = sendZDPCommand(bindingTarget, cid, bindReq);
        
        MgmtRsp rsp = new MgmtRsp();
        rsp.drain(ZDPCommandPacket.toFrameBuffer(packet));
        return rsp.getStatus();
    }
    
    //
    // Discovery Client Service
    //
    
    public AddrRsp zdpNetworkAddressRequest(
            ZigBeeAddress address,
            IEEEAddress target,
            boolean extResponse,
            int startIndex)
            throws IOException {
        AddrReq nwkAddrReq = new AddrReq(AddrReq.REQ_NWK);
        nwkAddrReq.setAddress(target);
        nwkAddrReq.setRequestType(
                extResponse ?
                AddrReq.REQUEST_TYPE_EXTENDED :
                AddrReq.REQUEST_TYPE_SINGLE);
        nwkAddrReq.setStartIndex(startIndex);
        
        ZDPCommandPacket zdpCommand = sendZDPCommand(address, ZDPCommand.ZDP_NWK_ADDR_REQ, nwkAddrReq);
        
        AddrRsp nwkAddrRsp = new AddrRsp();
        nwkAddrRsp.drain(ZDPCommandPacket.toFrameBuffer(zdpCommand));
        
        return nwkAddrRsp;
    }

    public AddrRsp zdpIEEEAddressRequest(
            ZigBeeAddress address,
            NetworkAddress target,
            boolean extResponse,
            int startIndex)
            throws IOException {
        AddrReq ieeeAddrReq = new AddrReq(AddrReq.REQ_IEEE);
        ensureUnicast(target);
        ieeeAddrReq.setAddress(target);
        ieeeAddrReq.setRequestType(
                extResponse ?
                AddrReq.REQUEST_TYPE_EXTENDED :
                AddrReq.REQUEST_TYPE_SINGLE);
        ieeeAddrReq.setStartIndex(startIndex);
        
        ensureUnicast(address);
        ZDPCommandPacket zdpCommand = sendZDPCommand(address, ZDPCommand.ZDP_IEEE_ADDR_REQ, ieeeAddrReq);
        
        AddrRsp ieeeAddrRsp = new AddrRsp();
        ieeeAddrRsp.drain(ZDPCommandPacket.toFrameBuffer(zdpCommand));
        
        return ieeeAddrRsp;
    }

    public MatchDescRsp zdpMatchDescriptorRequest(
            ZigBeeAddress address,
            NetworkAddress target,
            short profileId,
            short[] inClusterList,
            short[] outClusterList) throws IOException {
        MatchDescReq matchDescReq = new MatchDescReq();
        ensureUnicast(target);
        matchDescReq.setNetworkAddr(target);
        matchDescReq.setProfileID(profileId);
        matchDescReq.setInClusterList(inClusterList);
        matchDescReq.setOutClusterList(outClusterList);
        
        ensureUnicast(address);
        ZDPCommandPacket zdpCommand = sendZDPCommand(address, ZDPCommand.ZDP_MATCH_DESC_REQ, matchDescReq);
        
        MatchDescRsp matchDescRsp = new MatchDescRsp();
        matchDescRsp.drain(ZDPCommandPacket.toFrameBuffer(zdpCommand));
        
        return matchDescRsp;
    }

    public NodeDescRsp zdpNodeDescriptorResquest(
            ZigBeeAddress address,
            NetworkAddress target)
            throws IOException {
        DescReq descReq = new DescReq();
        descReq.setNetworkAddr(target);
        
        ensureUnicast(address);
        ZDPCommandPacket zdpCommand = sendZDPCommand(address, ZDPCommand.ZDP_NODE_DESC_REQ, descReq);
        
        NodeDescRsp nodeDescRsp = new NodeDescRsp();
        nodeDescRsp.drain(ZDPCommandPacket.toFrameBuffer(zdpCommand));
        
        return nodeDescRsp;
    }

    public ZDPCommandPacket zdpNodePowerDescriptorResquest(
            ZigBeeAddress address,
            NetworkAddress target)
            throws IOException {
        DescReq descReq = new DescReq();
        descReq.setNetworkAddr(target);
        
        ensureUnicast(address);
        ZDPCommandPacket zdpCommand = sendZDPCommand(address, ZDPCommand.ZDP_POWER_DESC_REQ, descReq);
        
        // TODO:
        return zdpCommand;
    }

    public SimpleDescRsp zdpSimpleDescriptorResquest(
            ZigBeeAddress address,
            NetworkAddress target,
            int endpoint)
            throws IOException {
        SimpleDescReq simpleDescReq = new SimpleDescReq();
        simpleDescReq.setNetworkAddr(target);
        simpleDescReq.setEndpoint(endpoint);
        
        ensureUnicast(address);
        ZDPCommandPacket zdpCommand = sendZDPCommand(address, ZDPCommand.ZDP_SIMPLE_DESC_REQ, simpleDescReq);
        
        SimpleDescRsp simpleDescRsp = new SimpleDescRsp();
        simpleDescRsp.drain(ZDPCommandPacket.toFrameBuffer(zdpCommand));
        
        return simpleDescRsp;
    }

    public void zdpExtendedSimpleDescriptorResquest(
            ZigBeeAddress address,
            NetworkAddress target,
            int endpoint,
            int startIndex)
            throws IOException {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public ActiveEpRsp zdpActiveEndpointResquest(
            ZigBeeAddress address,
            NetworkAddress target)
            throws IOException {
        DescReq descReq = new DescReq();
        descReq.setNetworkAddr(target);
        
        ensureUnicast(address);
        ZDPCommandPacket commandPacket = sendZDPCommand(address, ZDPCommand.ZDP_ACTIVE_EP_REQ, descReq);
        
        ActiveEpRsp activeEpRsp = new ActiveEpRsp();
        activeEpRsp.drain(ZDPCommandPacket.toFrameBuffer(commandPacket));
        
        return activeEpRsp;
    }

    public void zdpExtendedActiveEndpointResquest(
            ZigBeeAddress address,
            NetworkAddress target,
            int startIndex)
            throws IOException {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public ZDPCommandPacket zdpComplexDescriptorRequest(
            ZigBeeAddress address,
            NetworkAddress target)
            throws IOException {
        DescReq descReq = new DescReq();
        descReq.setNetworkAddr(target);
        
        ensureUnicast(address);
        ZDPCommandPacket zdpCommand = sendZDPCommand(address, ZDPCommand.ZDP_COMPLEX_DESC_REQ, descReq);
        
        // TODO:
        return zdpCommand;
    }

    public ZDPCommandPacket zdpUserDescriptorRequest(
            ZigBeeAddress address,
            NetworkAddress target)
            throws IOException {
        DescReq descReq = new DescReq();
        descReq.setNetworkAddr(target);
        
        ensureUnicast(address);
        ZDPCommandPacket zdpCommand = sendZDPCommand(address, ZDPCommand.ZDP_USER_DESC_REQ, descReq);
        
        // TODO:
        return zdpCommand;
    }

    public void zdpUserDescriptorSet(
            ZigBeeAddress address,
            NetworkAddress target)
            throws IOException {
        throw new UnsupportedOperationException("Not yet implemented.");
    }
    
    //
    // Node Client Service
    //
    
    public byte zdpMgmtLeaveRequest(ZigBeeAddress address, IEEEAddress deviceAddress, boolean removeChildren, boolean rejoin) throws IOException {
        MgmtLeaveReq leaveReq = new MgmtLeaveReq();
        leaveReq.setDeviceAddress(deviceAddress);
        leaveReq.setRemoveChildren(removeChildren);
        leaveReq.setRejoin(rejoin);
        ensureUnicast(address);
        ZDPCommandPacket packet = sendZDPCommand(address, ZDPCommand.ZDP_MGMT_LEAVE_REQ, leaveReq);
        MgmtRsp rsp = new MgmtRsp();
        rsp.drain(ZDPCommandPacket.toFrameBuffer(packet));
        return rsp.getStatus();
    }
}
