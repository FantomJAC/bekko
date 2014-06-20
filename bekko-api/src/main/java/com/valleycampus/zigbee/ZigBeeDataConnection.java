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
package com.valleycampus.zigbee;

import java.io.IOException;
import javax.microedition.io.Connection;

/**
 * Interface for ZigBee APS Data Entity layer connectivity.
 * 
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public interface ZigBeeDataConnection extends Connection {
    
    public static final int OPTION_SECURITY = 0x01;
    public static final int OPTION_USE_NWKKY = 0x02;
    public static final int OPTION_ACK = 0x04;
    public static final int OPTION_FRAGMENTATION = 0x08;
    
    public int getMaxSinglePayloadLength();
    
    public ZigBeeSimpleDescriptor getSimpleDescriptor();
    
    /**
     * Create a single ZigBee data packet for a transmission.
     * 
     * @return 
     */
    public ZigBeeDataPacket createZigBeeDataPacket();
    
    /**
     * Perform APSDE-DATA.request.
     * 
     * @param dataPacket
     * @param radius
     * @param txOptions
     * @throws IOException 
     */
    public void send(ZigBeeDataPacket dataPacket, int radius, int txOptions) throws IOException;
    
    /**
     * Wait for APSDE-DATA.inidication.
     * 
     * @param dataPacket
     * @param timeout
     * @throws IOException 
     */
    public void receive(ZigBeeDataPacket dataPacket, int timeout) throws IOException;
}
