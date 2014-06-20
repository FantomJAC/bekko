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
package com.valleycampus.zigbee.apl;

import com.valleycampus.zigbee.ZigBeeSimpleDescriptor;
import java.util.Properties;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class ZigBeeSimpleDescriptorImpl implements ZigBeeSimpleDescriptor {

    public static final String PREFIX = "zigbee.application";
    public static final String ENDPOINT = "endpoint";
    public static final String PROFILE_ID = "profileId";
    public static final String DEVICE_ID = "deviceId";
    public static final String DEVICE_VERSION = "deviceVersion";
    public static final String INPUT_CLUSTER_COUNT = "inputClusterCount";
    public static final String INPUT_CLUSTER = "inputCluster";
    public static final String OUTPUT_CLUSTER_COUNT = "outputClusterCount";
    public static final String OUTPUT_CLUSTER = "outputCluster";
    
    private Properties properties;
    private int index;
    
    public ZigBeeSimpleDescriptorImpl(Properties properties, int index) {
        this.properties = properties;
        this.index = index;
    }
    
    private String getProperty(String key) {
        return properties.getProperty(PREFIX + index + "." + key);
    }
    
    public int getEndpoint() {
        return Integer.parseInt(getProperty(ENDPOINT), 16);
    }

    public short getApplicationProfileIdentifier() {
        return (short) Integer.parseInt(getProperty(PROFILE_ID), 16);
    }

    public short getApplicationDeviceIdentifier() {
        return (short) Integer.parseInt(getProperty(DEVICE_ID), 16);
    }

    public byte getApplicationDeviceVersion() {
        return (byte) Integer.parseInt(getProperty(DEVICE_VERSION), 16);
    }
    
    private int getClusterCount(String key) {
        return Integer.parseInt(getProperty(key));
    }
    
    private short[] getClusterList(String countKey, String clusterKey) {
        int count = getClusterCount(countKey);
        short[] clusters = new short[count];
        for (int i = 0; i < count; i++) {
            clusters[0] = (short) Integer.parseInt(getProperty(clusterKey + i), 16);
        }
        return clusters;
    }

    public int getApplicationInputClusterCount() {
        return getClusterCount(INPUT_CLUSTER_COUNT);
    }

    public short[] getApplicationInputClusterList() {
        return getClusterList(INPUT_CLUSTER_COUNT, INPUT_CLUSTER);
    }

    public int getApplicationOutputClusterCount() {
       return getClusterCount(OUTPUT_CLUSTER_COUNT);
    }

    public short[] getApplicationOutputClusterList() {
        return getClusterList(OUTPUT_CLUSTER_COUNT, OUTPUT_CLUSTER);
    }
    
}
