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

import com.valleycampus.zigbee.ZigBeeSimpleDescriptor;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class ZigBeeSimpleDescriptorImpl implements ZigBeeSimpleDescriptor {
    
    private int endpoint;
    private short profileId;
    private short deviceId;
    private byte deviceVersion;
    private short[] inputClusters;
    private short[] outputClusters;
    
    public ZigBeeSimpleDescriptorImpl(
            int endpoint,
            short profileId,
            short deviceId,
            byte deviceVersion,
            short[] inputClusters,
            short[] outputClusters) {
        this.endpoint = endpoint;
        this.profileId = profileId;
        this.deviceId = deviceId;
        this.deviceVersion = deviceVersion;
        this.inputClusters = inputClusters;
        this.outputClusters = outputClusters;
    }
    
    private ZigBeeSimpleDescriptorImpl() {
        
    }
    
    public static ZigBeeSimpleDescriptor clone(ZigBeeSimpleDescriptor descriptor) {
        return clone(descriptor.getEndpoint(), descriptor);
    }
    
    public static ZigBeeSimpleDescriptor clone(int endpoint, ZigBeeSimpleDescriptor descriptor) {
        ZigBeeSimpleDescriptorImpl impl = new ZigBeeSimpleDescriptorImpl();
        impl.endpoint = endpoint;
        impl.profileId = descriptor.getApplicationProfileIdentifier();
        impl.deviceId = descriptor.getApplicationDeviceIdentifier();
        impl.deviceVersion = descriptor.getApplicationDeviceVersion();
        int inputClusterCount = descriptor.getApplicationInputClusterCount();
        impl.inputClusters = new short[inputClusterCount];
        if (inputClusterCount > 0) {
            System.arraycopy(descriptor.getApplicationInputClusterList(), 0, impl.inputClusters, 0, inputClusterCount);
        }
        int outputClusterCount = descriptor.getApplicationOutputClusterCount();
        impl.outputClusters = new short[outputClusterCount];
        if (outputClusterCount > 0) {
            System.arraycopy(descriptor.getApplicationOutputClusterList(), 0, impl.outputClusters, 0, outputClusterCount);
        }
        return impl;
    }

    public int getEndpoint() {
        return endpoint;
    }

    public short getApplicationProfileIdentifier() {
        return profileId;
    }

    public short getApplicationDeviceIdentifier() {
        return deviceId;
    }

    public byte getApplicationDeviceVersion() {
        return deviceVersion;
    }

    public int getApplicationInputClusterCount() {
        return (inputClusters != null) ? inputClusters.length : 0;
    }

    public short[] getApplicationInputClusterList() {
        return (inputClusters != null) ? inputClusters : new short[0];
    }

    public int getApplicationOutputClusterCount() {
        return (outputClusters != null) ? outputClusters.length : 0;
    }

    public short[] getApplicationOutputClusterList() {
        return (outputClusters != null) ? outputClusters : new short[0];
    }
    
}
