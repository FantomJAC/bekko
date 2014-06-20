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
package com.valleycampus.zigbee;

import java.util.Dictionary;

/**
 * Entry point for ZigBee Application framework.
 * 
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public interface ApplicationService {
    
    /**
     * Register the endpoint with a SimpleDescriptor.
     * 
     * @param simpleDescriptor
     * @param properties
     * @throws ApplicationException ApplicationException if the application has been deployed.
     */
    public void registerEndpoint(ZigBeeSimpleDescriptor simpleDescriptor,
                                Dictionary properties)
                         throws ApplicationException;
    
    /**
     * Unregister the endpoint.
     * 
     * @param endpoint
     * @throws ApplicationException 
     */
    public void unregisterEndpoint(int endpoint) throws ApplicationException;
}
