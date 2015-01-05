/*
 * Copyright (C) 2014 Shotaro Uchida
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
package com.valleycampus.xbee.api;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.io.IOException;

/**
 *
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public class RXTXDriverContext {
    
    private final String portName;
    private final SerialPort comPort;
    private final XBeeDriver driver;
    
    private RXTXDriverContext(String portName, SerialPort comPort, XBeeDriver driver) {
        this.portName = portName;
        this.comPort = comPort;
        this.driver = driver;
    }
    
    public static RXTXDriverContext openDriver(String portName) throws Exception {
        CommPortIdentifier comId = CommPortIdentifier.getPortIdentifier(portName);
        SerialPort comPort = (SerialPort) comId.open(XBeeDriver.class.getName(), 0);
        comPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        comPort.setRTS(true);
        XBeeDriver driver = new XBeeDriver(
                comPort.getInputStream(),
                comPort.getOutputStream());
        driver.activate();
        return new RXTXDriverContext(portName, comPort, driver);
    }
    
    public String getPortName() {
        return portName;
    }
    
    public XBeeAPI getXBeeAPI() {
        return driver;
    }
    
    public void closeDriver() throws IOException {
        driver.shutdown();
        comPort.close();
    }
}
