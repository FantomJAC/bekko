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
package com.valleycampus.xbee;

import com.valleycampus.zigbee.ZigBeeConst;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class XBeeBindingManager {
    
    private Set bindingTable;
    private final Object bindLock = new Object();
    
    protected List getBindedInfos(int clusterID, int endpoint) {
        Vector vector = new Vector();
        synchronized (bindLock) {
            for (Iterator it = bindingTable.iterator(); it.hasNext();) {
                BindInfo bindInfo = (BindInfo) it.next();
                if (bindInfo.getClusterID() == clusterID &&
                    bindInfo.getSourceEndpoint() == endpoint) {
                    vector.add(bindInfo);
                }
            }
        }
        return vector;
    }
    
    public byte bind(BindInfo bindInfo) {
        if (!bindInfo.isValid()) {
            return ZigBeeConst.ILLEGAL_REQUEST;
        }
        synchronized (bindLock) {
            bindingTable.add(bindInfo);
        }
        return ZigBeeConst.SUCCESS;
    }

    public byte unbind(BindInfo bindInfo) {
        boolean b;
        synchronized (bindLock) {
            b = bindingTable.remove(bindInfo);
        }
        return b ? ZigBeeConst.SUCCESS : ZigBeeConst.INVALID_BINDING;
    }
}
