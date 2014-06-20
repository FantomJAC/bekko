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
package com.valleycampus.zigbee.zcl.core;

import com.valleycampus.zigbee.ZigBeeAddress;
import com.valleycampus.zigbee.ZigBeeDataPacket;
import com.valleycampus.zigbee.zcl.ZCLCluster;
import com.valleycampus.zigbee.zcl.ZCLCommandPacket;
import com.valleycampus.zigbee.zcl.attribute.IntegerDataType;
import com.valleycampus.zigbee.zcl.attribute.MiscellaneousDataType;
import com.valleycampus.zigbee.zcl.attribute.StringDataType;
import com.valleycampus.zigbee.zcl.command.ZCLCommand;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import com.valleycampus.zigbee.io.ByteUtil;
import com.valleycampus.zigbee.util.Sequence;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class ZCLContext {
    
    public static final String ZCL_PASSTHROUGH_GENERAL_COMMANDS = "com.valleycampus.zigbee.zcl.passthroughGeneralCommands";

    private ArrayList clusterList;
    private Sequence transactionSequence;
    private static PrintStream logStream = System.out;
    private static volatile boolean debugEnabled;
    
    static {
        try {
            Class.forName(IntegerDataType.class.getName());
            Class.forName(StringDataType.class.getName());
            Class.forName(MiscellaneousDataType.class.getName());
        } catch (ClassNotFoundException ex) {
        }
    }
    
    public ZCLContext() {
        clusterList = new ArrayList();
        transactionSequence = new Sequence(8);
    }
    
    public static void setLogStream(PrintStream logStream) {
        ZCLContext.logStream = logStream;
    }
    
    public static void setDebugEnabled(boolean debugEnabled) {
        ZCLContext.debugEnabled = debugEnabled;
    }
    
    protected static void debug(String s) {
        if (debugEnabled) {
            logStream.println("[ZCL#debug] " + s);
        }
    }
    
    protected static void warn(String s) {
        logStream.println("[ZCL#warn] " + s);
    }
    
    protected static void error(String s, Exception ex) {
        logStream.println("[ZCL#error] " + s);
        if (debugEnabled) {
            ex.printStackTrace();
        }
    }
    
    protected static void debug(ZigBeeDataPacket dataPacket, ZCLCommandPacket commandPacket, boolean resp) {
        ZigBeeAddress remoteAddress = dataPacket.getAddress();
        int remoteEndpoint = dataPacket.getEndpoint();
        StringBuffer sb = new StringBuffer();
        sb.append("[Seq #").append(commandPacket.getTsn()).append("] ");
        if (resp) {
            sb
            .append(remoteAddress.toString()).append(":").append(remoteEndpoint)
            .append(" -> ")
            .append(dataPacket.getLocalAddress()).append(":").append(dataPacket.getLocalEndpoint())
            .append(" Cluster 0x").append(ByteUtil.toHexString(dataPacket.getClusterId()))
            .append(" >>(").append(commandPacket.getPayloadLength()).append(") ");
        } else {
            if (remoteAddress == null) {
                sb.append("[BoundDevice]");
            } else {
                sb.append(remoteAddress.toString()).append(":").append(remoteEndpoint);
            }
            sb
            .append(" <- ")
            .append(dataPacket.getLocalAddress()).append(":").append(dataPacket.getLocalEndpoint())
            .append(" Cluster 0x").append(ByteUtil.toHexString(dataPacket.getClusterId()))
            .append(" <<(").append(commandPacket.getPayloadLength()).append(") ");
        }
        byte[] buffer = commandPacket.getPayload();
        for (int i = commandPacket.getPayloadOffset(); i < commandPacket.getPayloadOffset() + commandPacket.getPayloadLength(); i++) {
            sb.append(ByteUtil.toHexString(buffer[i])).append(" ");
        }
        debug(sb.toString());
    }

    /**
     * Handle the ZCL message.
     * 
     * @param request an inbound ZCL message.
     * @param response an outbound ZCL message.
     * @return true if response is available
     */
    public boolean handle(ZigBeeDataPacket request, ZigBeeDataPacket response) {
        ZCLCommandPacket zclRequest = ZCLCommandPacketImpl.toZCLCommandPacket(request);

        // Lookup for related cluster implementation.
        final ZCLClusterSupport clusterSupport =
                lookupCluster(zclRequest.getClusterId(),
                        zclRequest.isFromServer() ?
                        ZCLCluster.DIRECTION_OUTPUT :
                        ZCLCluster.DIRECTION_INPUT);

        if (clusterSupport != null) {
            ZCLContext.debug(request, zclRequest, true);
            ZCLCommandPacket zclResponse = clusterSupport.doResponse(zclRequest, request.getLocalAddress().isUnicast());
            if (zclResponse != null) {
                // Need response
                ZCLCommandPacketImpl.setupSendZigBeeDataPacket(response, zclResponse);
                ZCLContext.debug(response, zclResponse, false);
                return true;
            }
        } else {
            debug("Message discarded, no related cluster was found.");
        }
        
        return false;
    }

    /**
     * Prepare for next attribute reporting.
     * 
     * @return List contains ZCLCommandPacket ready to send.
     */
    public List processNextReports() {
        List reports = new ArrayList();
        for (int i = 0; i < clusterList.size(); i++) {
            ZCLClusterSupport clusterSupport = (ZCLClusterSupport) clusterList.get(i);
            ZCLCommand report = clusterSupport.doReportAttributes();
            if (report != null) {
                ZCLCommandPacketImpl response = (ZCLCommandPacketImpl) createZCLCommandPacket(clusterSupport.getCluster());
                response.setZCLCommand(report);
                response.setClusterSpecific(false);
                response.setManufacturerSpecific(false);
                response.setAddress(null);
                response.setEndpoint(-1);
                reports.add(response);
            }
        }
        return reports;
    }

    public void attachZCLCluster(ZCLCluster cluster, Dictionary properties) {
        if (lookupCluster(cluster.getClusterId(), cluster.getDirection()) != null) {
            return;
        }
        clusterList.add(new ZCLClusterSupport(cluster, properties));
    }
    
    public ZCLCommandPacket createZCLCommandPacket(short clusterId, byte direction) {
        ZCLClusterSupport clusterSupport = lookupCluster(clusterId, direction);
        if (clusterSupport != null) {
            return createZCLCommandPacket(clusterSupport.getCluster());
        }
        return null;
    }
    
    public ZCLCommandPacket createZCLCommandPacket(ZCLCluster cluster) {
        ZCLCommandPacketImpl commandPacket = new ZCLCommandPacketImpl();
        commandPacket.setClusterId(cluster.getClusterId());
        commandPacket.setTsn(transactionSequence.nextSequence());
        commandPacket.setFromServer(cluster.getDirection() == ZCLCluster.DIRECTION_INPUT);
        return commandPacket;
    }
    
    public ZCLCluster getZCLCluster(short clusterId, byte direction) {
        ZCLClusterSupport clusterSupport = lookupCluster(clusterId, direction);
        if (clusterSupport != null) {
            return clusterSupport.getCluster();
        }
        return null;
    }
    
    public ZCLCluster[] getZCLClusters() {
        ZCLCluster[] clusters = new ZCLCluster[clusterList.size()];
        for (int i = 0; i < clusterList.size(); i++) {
            ZCLClusterSupport clusterSupport = (ZCLClusterSupport) clusterList.get(i);
            clusters[i] = clusterSupport.getCluster();
        }
        return clusters;
    }
    
    private ZCLClusterSupport lookupCluster(short clusterId, byte direction) {
        for (int i = 0; i < clusterList.size(); i++) {
            ZCLClusterSupport clusterSupport = (ZCLClusterSupport) clusterList.get(i);
            ZCLCluster cluster = clusterSupport.getCluster();
            if (cluster.getClusterId() == clusterId && cluster.getDirection() == direction) {
                return clusterSupport;
            }
        }
        return null;
    }
}
