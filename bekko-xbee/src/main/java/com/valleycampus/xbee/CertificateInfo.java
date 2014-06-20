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
public class CertificateInfo {
    
    private byte[] caPublicKey;
    private byte[] deviceImplicitCert;
    private byte[] devicePrivateKey;
    private byte[] devicePublicKey;

    /**
     * @return the caPublicKey
     */
    public byte[] getCAPublicKey() {
        return caPublicKey;
    }

    /**
     * @param caPublicKey the caPublicKey to set
     */
    public void setCAPublicKey(byte[] caPublicKey) {
        this.caPublicKey = caPublicKey;
    }

    /**
     * @return the deviceImplicitCert
     */
    public byte[] getDeviceImplicitCert() {
        return deviceImplicitCert;
    }

    /**
     * @param deviceImplicitCert the deviceImplicitCert to set
     */
    public void setDeviceImplicitCert(byte[] deviceImplicitCert) {
        this.deviceImplicitCert = deviceImplicitCert;
    }

    /**
     * @return the devicePrivateKey
     */
    public byte[] getDevicePrivateKey() {
        return devicePrivateKey;
    }

    /**
     * @param devicePrivateKey the devicePrivateKey to set
     */
    public void setDevicePrivateKey(byte[] devicePrivateKey) {
        this.devicePrivateKey = devicePrivateKey;
    }

    /**
     * @return the devicePublicKey
     */
    public byte[] getDevicePublicKey() {
        return devicePublicKey;
    }

    /**
     * @param devicePublicKey the devicePublicKey to set
     */
    public void setDevicePublicKey(byte[] devicePublicKey) {
        this.devicePublicKey = devicePublicKey;
    }
}
