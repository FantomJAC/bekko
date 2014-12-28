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
package com.valleycampus.xbee;

import com.valleycampus.xbee.api.XBeeAPI;
import com.valleycampus.xbee.api.XBeeDriver;
import com.valleycampus.xbee.api.XBeeIO;
import com.valleycampus.xbee.digimesh.DigiMeshDevice;
import com.valleycampus.zigbee.zdo.ZigBeeDevice;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.io.IOException;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class DriverManager {
    
    private SerialPort comPort;
    private XBeeDriver driver;
    
    public ZigBeeDevice openDriver(String portName) throws Exception {
        CommPortIdentifier comId = CommPortIdentifier.getPortIdentifier(portName);
        comPort = (SerialPort) comId.open(XBeeDriver.class.getName(), 0);
        comPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        comPort.setRTS(true);
        driver = new XBeeDriver(
                comPort.getInputStream(),
                comPort.getOutputStream());
        driver.activate();
        return createZigBeeDevice(driver);
    }
    
    public void closeDriver() throws IOException {
        driver.shutdown();
        comPort.close();
    }
    
    private static ZigBeeDevice createZigBeeDevice(XBeeAPI xbAPI) throws IOException {
        XBeeIO xbIO = new XBeeIO(xbAPI);
        xbIO.setTimeout(3000);

        int hv = xbIO.read16("HV");
        switch (hv & XBeeAPI.HV_MASK) {
        case XBeeAPI.HV_S2:
        case XBeeAPI.HV_S2_PRO:
        case XBeeAPI.HV_S2B_PRO:
        case 0x2100:
        case 0x2200:
            XBeeDevice.debug("ZigBee(S2) device is detected.");
            return XBeeDevice.createXBeeDevice(xbAPI);
        case 0x2300:
            XBeeDevice.debug("DigiMesh(S3) device is detected.");
        case 0x2400:
            XBeeDevice.debug("DigiMesh(S8) device is detected.");
            return DigiMeshDevice.createDigiMeshDevice(xbAPI);
        default:
            throw new IOException("Module " + Integer.toHexString(hv) + " not support ZigBee");
        }
    }
    
}
