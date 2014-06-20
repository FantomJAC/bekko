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
package com.valleycampus.xbee.api.s6;

import com.valleycampus.xbee.api.XBeeRequest;
import com.valleycampus.zigbee.io.FrameBuffer;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class TransmitRequestIPv4 extends XBeeRequest {
 
    public static final byte PROTOCOL_UDP = 0;
    public static final byte PROTOCOL_TCP = 1;
    
    private int address32;
    private int destinationPort;
    private int sourcePort;
    private byte protocol;
    private byte transmitOptions;
    private byte[] data;
    
    public TransmitRequestIPv4() {
        setFrameType(API_TX_REQ_IP_V4);
    }

    public void pull(FrameBuffer frameBuffer) {
        super.pull(frameBuffer);
        frameBuffer
            .putInt32(getAddress32())
            .putInt16(getDestinationPort())
            .putInt16(getSourcePort())
            .put(getProtocol())
            .put(getTransmitOptions());
        if (getData() != null) {
            frameBuffer.put(getData());
        }
    }

    public int quote() {
        int q = super.quote() + 10;
        if (getData() != null) {
            q += getData().length;
        }
        return q;
    }

    /**
     * @return the address32
     */
    public int getAddress32() {
        return address32;
    }

    /**
     * @param address32 the address32 to set
     */
    public void setAddress32(int address32) {
        this.address32 = address32;
    }

    /**
     * @return the destinationPort
     */
    public int getDestinationPort() {
        return destinationPort;
    }

    /**
     * @param destinationPort the destinationPort to set
     */
    public void setDestinationPort(int destinationPort) {
        this.destinationPort = destinationPort;
    }

    /**
     * @return the sourcePort
     */
    public int getSourcePort() {
        return sourcePort;
    }

    /**
     * @param sourcePort the sourcePort to set
     */
    public void setSourcePort(int sourcePort) {
        this.sourcePort = sourcePort;
    }

    /**
     * @return the protocol
     */
    public byte getProtocol() {
        return protocol;
    }

    /**
     * @param protocol the protocol to set
     */
    public void setProtocol(byte protocol) {
        this.protocol = protocol;
    }

    /**
     * @return the transmitOptions
     */
    public byte getTransmitOptions() {
        return transmitOptions;
    }

    /**
     * @param transmitOptions the transmitOptions to set
     */
    public void setTransmitOptions(byte transmitOptions) {
        this.transmitOptions = transmitOptions;
    }

    /**
     * @return the data
     */
    public byte[] getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(byte[] data) {
        this.data = data;
    }
}
