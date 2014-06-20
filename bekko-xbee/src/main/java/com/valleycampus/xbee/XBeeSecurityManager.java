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

import com.valleycampus.xbee.api.XBeeIO;
import java.io.IOException;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class XBeeSecurityManager {
    
    private XBeeIO xbIO;
    private boolean se;
    
    public XBeeSecurityManager(XBeeIO xbIO, boolean se) {
        this.xbIO = xbIO;
        this.se = se;
    }
    
    public boolean isSE() {
        return se;
    }
    
    public void initSecurity(SecurityInfo securityInfo) throws IOException {
        if (securityInfo != null) {
            if (!isSE()) {
                xbIO.write8("EE", 1);
                int eo = 0;
                if (securityInfo.isUnsecuredJoin()) {
                    eo |= 0x01;
                }
                if (securityInfo.isTrustCenter()) {
                    eo |= 0x02;
                }
                xbIO.write8("EO", eo);
            }
            if (securityInfo.getNetworkKey() != null) {
                xbIO.writeMulti8("NK", securityInfo.getNetworkKey());
            }
            if (securityInfo.getLinkKey() != null) {
                xbIO.writeMulti8("KY", securityInfo.getLinkKey());
            }
        } else {
            if (!isSE()) {
                xbIO.write8("EE", 0);
            }
        }
    }
    
    public void installCertificateInfo(CertificateInfo ci) throws IOException {
        if (!isSE()) {
            throw new UnsupportedOperationException("Cert is SE only");
        }
        xbIO.writeMulti8("ZU", ci.getCAPublicKey());
        xbIO.writeMulti8("ZT", ci.getDeviceImplicitCert());
        xbIO.writeMulti8("ZV", ci.getDevicePrivateKey());
        xbIO.write();
    }
    
    public void setInstallationCode(byte[] ic) throws IOException {
        if (!isSE()) {
            throw new UnsupportedOperationException("Installation Code is SE only");
        }
        xbIO.writeMulti8("IN", ic);
    }
}
