/*
 * Copyright (C) 2014 Valley Campus Japan, Inc.
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
package com.valleycampus.zigbee.io;

/**
 * Common protocol frame interface.
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public interface Frame {

    /**
     * Read this entire frame packet.
     * @param buffer buffer to read entire frame.
     * @param offset buffer offset.
     */
    public void pull(FrameBuffer frameBuffer);

    /**
     * Quote this total packet byte-size.
     * @return
     */
    public int quote();

    /**
     * Drain buffer to this frame.
     * @param frameBuffer
     */
    public void drain(FrameBuffer frameBuffer);
}
