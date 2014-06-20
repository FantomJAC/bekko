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

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class SecurityInfo {
    
    private byte[] networkKey;
    private byte[] linkKey;
    private boolean trustCenter;
    private boolean unsecuredJoin;

    /**
     * @return the networkKey
     */
    public byte[] getNetworkKey() {
        return networkKey;
    }

    /**
     * @param networkKey the networkKey to set
     */
    public void setNetworkKey(byte[] networkKey) {
        this.networkKey = networkKey;
    }

    /**
     * @return the linkKey
     */
    public byte[] getLinkKey() {
        return linkKey;
    }

    /**
     * @param linkKey the linkKey to set
     */
    public void setLinkKey(byte[] linkKey) {
        this.linkKey = linkKey;
    }

    /**
     * @return the trustCenter
     */
    public boolean isTrustCenter() {
        return trustCenter;
    }

    /**
     * @param trustCenter the trustCenter to set
     */
    public void setTrustCenter(boolean trustCenter) {
        this.trustCenter = trustCenter;
    }

    /**
     * @return the unsecuredJoin
     */
    public boolean isUnsecuredJoin() {
        return unsecuredJoin;
    }

    /**
     * @param unsecuredJoin the unsecuredJoin to set
     */
    public void setUnsecuredJoin(boolean unsecuredJoin) {
        this.unsecuredJoin = unsecuredJoin;
    }
}
