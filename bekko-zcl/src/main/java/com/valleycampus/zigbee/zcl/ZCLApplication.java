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
package com.valleycampus.zigbee.zcl;

import com.valleycampus.zigbee.ZigBeeConst;
import com.valleycampus.zigbee.ZigBeeDataConnection;
import com.valleycampus.zigbee.ZigBeeDataPacket;
import com.valleycampus.zigbee.ZigBeeException;
import com.valleycampus.zigbee.zcl.core.ZCLContext;
import com.valleycampus.zigbee.zcl.core.ZCLCommandPacketImpl;
import java.io.IOException;
import java.util.List;
import com.valleycampus.zigbee.service.ServiceTask;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public abstract class ZCLApplication extends ServiceTask {

    public static final int DEFAULT_TX_RADIUS = 12;
    public static final int DEFAULT_TX_OPTIONS = ZigBeeDataConnection.OPTION_ACK;
    private ZCLContext context;
    private ZigBeeDataConnection connection;

    protected ZCLApplication(ZigBeeDataConnection connection) {
        super("ZCL Application Container EP#" + connection.getSimpleDescriptor().getEndpoint());
        this.connection = connection;
    }
    
    protected abstract ZCLContext createContext();
    
    protected ZCLContext getZCLContext() {
        return context;
    }

    public boolean activate() {
        if (context != null) {
            return false;
        }
        context = createContext();
        return super.activate();
    }

    public boolean shutdown() {
        try {
            connection.close();
            return super.shutdown();
        } catch (IOException ex) {
            return false;
        }
    }
    
    public void sendZCLCommandPacket(ZCLCommandPacket commandPacket) throws IOException {
        sendZCLCommandPacket(commandPacket, DEFAULT_TX_RADIUS, DEFAULT_TX_OPTIONS);
    }
    
    public void sendZCLCommandPacket(ZCLCommandPacket commandPacket, int radius, int txOptions) throws IOException {
        ZigBeeDataPacket packet = connection.createZigBeeDataPacket();
        ZCLCommandPacketImpl.setupSendZigBeeDataPacket(packet, commandPacket);
        connection.send(packet, radius, txOptions);
        byte status = packet.getStatus();
        if (status != ZigBeeConst.SUCCESS) {
            throw new ZigBeeException(status);
        }
    }

    private void processPacket() throws IOException {
        ZigBeeDataPacket packet = connection.createZigBeeDataPacket();
        connection.receive(packet, 0);
        byte status = packet.getStatus();
        if (status != ZigBeeConst.SUCCESS) {
            throw new ZigBeeException(status);
        }
        ZigBeeDataPacket response = connection.createZigBeeDataPacket();
        if (context.handle(packet, response)) {
            connection.send(response, DEFAULT_TX_RADIUS, DEFAULT_TX_OPTIONS);
            status = response.getStatus();
            if (status != ZigBeeConst.SUCCESS) {
                throw new ZigBeeException(status);
            }
        }
    }

    private void processReports() throws IOException {
        List reports = context.processNextReports();
        for (int i = 0; i < reports.size(); i++) {
            ZCLCommandPacket commandPacket = (ZCLCommandPacket) reports.get(i);
            sendZCLCommandPacket(commandPacket);
        }
    }

    protected void taskLoop() {
        try {
            processPacket();
        } catch (IOException ex) {
            //ZCLApplication.error("Exception at Dispatcher", ex);
        }
        try {
            processReports();
        } catch (IOException ex) {
            //ZCLApplication.error("Exception at Dispatcher", ex);
        }
    }
}
