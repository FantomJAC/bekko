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
package com.valleycampus.zigbee.zdp.command;

import com.valleycampus.zigbee.io.FrameBuffer;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class GeneralRequest implements ZDPCommand {

    private byte[] command;
    
    public GeneralRequest(byte[] command) {
        this.command = command;
    }
    
    public void pull(FrameBuffer frameBuffer) {
        if (command != null) {
            frameBuffer.put(command);
        }
    }

    public int quote() {
        return command != null ? command.length : 0;
    }

    public void drain(FrameBuffer frameBuffer) {
        frameBuffer.get(command);
    }
    
}
