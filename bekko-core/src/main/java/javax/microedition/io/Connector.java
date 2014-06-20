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
package javax.microedition.io;


import com.valleycampus.zigbee.apl.BekkoConnector;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class Connector {
    
    public static final int READ = 1;
    public static final int READ_WRITE = 3;
    public static final int WRITE = 2;

    public static Connection open(String name) throws IOException {
        return open(name, READ_WRITE);
    }

    public static Connection open(String name, int mode) throws IOException {
        return open(name, mode, true);
    }

    public static Connection open(String name, int mode, boolean timeouts) throws IOException {
        return BekkoConnector.open(name, mode, timeouts);
    }
    
    public static DataInputStream openDataInputStream(String name) throws IOException {
        throw new ConnectionNotFoundException("Not Supported");
    }

    public static DataOutputStream openDataOutputStream(String name) throws IOException {
        throw new ConnectionNotFoundException("Not Supported");
    }

    public static InputStream openInputStream(String name) throws IOException {
        throw new ConnectionNotFoundException("Not Supported");
    }
}
