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
package com.valleycampus.zigbee.zcl.core;

import com.valleycampus.zigbee.zcl.RemoteAttributeCapability;
import com.valleycampus.zigbee.zcl.attribute.DataType;
import com.valleycampus.zigbee.zcl.ZCLAttribute;
import com.valleycampus.zigbee.zcl.ZCLCluster;
import com.valleycampus.zigbee.zcl.UnsupportedCommandException;
import com.valleycampus.zigbee.zcl.ZCLCommandAuthenticator;
import com.valleycampus.zigbee.zcl.ZCLCommandPacket;
import com.valleycampus.zigbee.zcl.command.ConfigureReporting;
import com.valleycampus.zigbee.zcl.command.ConfigureReportingResponse;
import com.valleycampus.zigbee.zcl.command.DefaultResponse;
import com.valleycampus.zigbee.zcl.command.DiscoverAttributesResponse;
import com.valleycampus.zigbee.zcl.command.ReadAttributes;
import com.valleycampus.zigbee.zcl.command.ReadAttributesResponse;
import com.valleycampus.zigbee.zcl.command.ReadReportingConfigurationResponse;
import com.valleycampus.zigbee.zcl.command.ReportAttributes;
import com.valleycampus.zigbee.zcl.command.WriteAttributes;
import com.valleycampus.zigbee.zcl.command.WriteAttributesResponse;
import com.valleycampus.zigbee.zcl.command.ZCLCommand;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Iterator;
import com.valleycampus.zigbee.util.Commons;

/**
 *
 * @author Shotaro Uchida <suchida@valleycampus.com>
 */
public class ZCLClusterSupport {

    private ZCLCluster cluster;
    private Dictionary properties;
    private HashSet reportingSet;
    
    public ZCLClusterSupport(ZCLCluster cluster, Dictionary properties) {
        this.cluster = cluster;
        this.properties = properties;
        reportingSet = new HashSet();
    }

    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + this.cluster.getClusterId();
        hash = 37 * hash + this.cluster.getDirection();
        return hash;
    }

    public boolean equals(Object obj) {
        if (obj instanceof ZCLClusterSupport) {
            ZCLClusterSupport e = (ZCLClusterSupport) obj;
            return e.cluster.getClusterId() == this.cluster.getClusterId() &&
                   e.cluster.getDirection() == this.cluster.getDirection();
        }
        return false;
    }
    
    public ZCLCluster getCluster() {
        return cluster;
    }
    
    protected boolean isPassthroughGeneralCommands() {
        if (properties != null) {
            Object val = properties.get(ZCLContext.ZCL_PASSTHROUGH_GENERAL_COMMANDS);
            if (val != null) {
                return Commons.parseBoolean(val.toString());
            }
        }
        return false;
    }
    
    protected ZCLCommandAuthenticator getZCLCommandAuthenticator() {
        if (properties != null) {
            Object val = properties.get(ZCLCommandAuthenticator.class.getName());
            if (val != null) {
                return (ZCLCommandAuthenticator) val;
            }
        }
        return null;
    }
    
    protected Object getProperty(Object key) {
        if (properties != null) {
            return properties.get(key);
        }
        return null;
    }
    
    protected boolean getBooleanProperty(Object key) {
        Object val = getProperty(key);
        return val != null && Commons.parseBoolean(val.toString());
    }

    private static ZCLCommandPacket doGeneralCommandResponse(ZCLCommandPacket request, ZCLCommand commandFrame) {
        ZCLCommandPacketImpl commandPacket = (ZCLCommandPacketImpl) ZCLCommandPacketImpl.createResponsePacket(request);
        commandPacket.setZCLCommand(commandFrame);
        commandPacket.setClusterSpecific(false);
        commandPacket.setManufacturerSpecific(false);
        return commandPacket;
    }
    
    private ZCLCommandPacket doReadAttributesResponse(ZCLCommandPacket request) {
        ReadAttributes readAttributes = new ReadAttributes();
        try {
            readAttributes.drain(ZCLCommandPacketImpl.getPayloadAsFrameBuffer(request));
        } catch (Exception ex) {
            return DefaultResponse.doDefaultResponse(request, ZCLCluster.STATUS_MALFORMED_COMMAND);
        }
        short[] attributesId = readAttributes.getAttributesId();
        int attributesLength = attributesId.length;
        ZCLContext.debug("Read Attribute Request: " + attributesLength + " Attributes");
        
        ReadAttributesResponse.StatusRecord[] statusRecords = new ReadAttributesResponse.StatusRecord[attributesLength];
        for (int i = 0; i < attributesLength; i++) {
            short attributeId = attributesId[i];
            ReadAttributesResponse.StatusRecord record = new ReadAttributesResponse.StatusRecord();
            record.setAttributeId(attributeId);
            ZCLAttribute attribute = cluster.getAttribute(attributeId);
            if (attribute == null) {
                record.setStatus(ZCLCluster.STATUS_UNSUPPORTED_ATTRIBUTE);
            } else {
                if (attribute.isReadable()) {
                    record.setDataType(attribute.getDataType());
                    try {
                        record.setData(attribute.getData());
                        record.setStatus(ZCLCluster.STATUS_SUCCESS);
                    } catch (IOException ex) {
                        record.setStatus(ZCLCluster.STATUS_FAILURE);
                    }
                } else {
                    record.setStatus(ZCLCluster.STATUS_WRITE_ONLY);
                }
            }
            statusRecords[i] = record;
        }
        
        ReadAttributesResponse readAttributesResponse = new ReadAttributesResponse();
        readAttributesResponse.setRecords(statusRecords);
        return doGeneralCommandResponse(request, readAttributes);
    }
    
    private ZCLCommandPacket doWriteAttributesResponse(ZCLCommandPacket request) {
        WriteAttributes writeAttributes = new WriteAttributes(request.getCommandId());
        try {
            writeAttributes.drain(ZCLCommandPacketImpl.getPayloadAsFrameBuffer(request));
        } catch (Exception ex) {
            return DefaultResponse.doDefaultResponse(request, ZCLCluster.STATUS_MALFORMED_COMMAND);
        }
        WriteAttributes.Record[] records = writeAttributes.getRecords();
        int attributesLength = records.length;
        ZCLContext.debug("Write Attribute Request: " + attributesLength + " Attributes");
        
        boolean allSuccess = true;
        WriteAttributesResponse.StatusRecord[] statusRecords = null;
        if (writeAttributes.getCommandId() == ZCLCommand.WRITE_ATTRIBUTES) {
            statusRecords = new WriteAttributesResponse.StatusRecord[attributesLength];
        }
        for (int i = 0; i < attributesLength; i++) {
            final byte status;
            WriteAttributes.Record record = records[i];
            short attributeId = record.getAttributeId();
            ZCLAttribute attribute = cluster.getAttribute(attributeId);
            if (attribute == null) {
                status = ZCLCluster.STATUS_UNSUPPORTED_ATTRIBUTE;
            } else if (record.getDataType() != attribute.getDataType()) {
                status = ZCLCluster.STATUS_INVALID_DATA_TYPE;
            } else if (!attribute.isWritable()) {
                status = ZCLCluster.STATUS_READ_ONLY;
            } else {
                boolean success;
                try {
                    attribute.setData(record.getData());
                    success = true;
                } catch (IOException ex) {
                    success = false;
                }
                status = success ? ZCLCluster.STATUS_SUCCESS : ZCLCluster.STATUS_FAILURE;
            }
            if (status != ZCLCluster.STATUS_SUCCESS) {
                allSuccess = false;
                if (writeAttributes.getCommandId() == ZCLCommand.WRITE_ATTRIBUTES_UNDIVIDED) {
                    // Suspend further writings
                    break;
                }
            }
            if (statusRecords != null) {
                statusRecords[i].setStatus(status);
                statusRecords[i].setAttributeId(attributeId);
            }
        }
        
        switch (writeAttributes.getCommandId()) {
        case ZCLCommand.WRITE_ATTRIBUTES_NO_RSP:
            // Send No response
            return null;
        case ZCLCommand.WRITE_ATTRIBUTES_UNDIVIDED:
            // I can't find any convention about the response of WriteAttributesUndivided
            // However, according to 2.4.12.2, DefaultResponse should be generated in such case.
            return DefaultResponse.doDefaultResponse(
                    request,
                    allSuccess ?
                        ZCLCluster.STATUS_SUCCESS :
                        ZCLCluster.STATUS_FAILURE);
        }
        
        // WriteAttributesResponse will be generated all other cases.
        WriteAttributesResponse writeAttributesResponse = new WriteAttributesResponse();
        writeAttributesResponse.setAllSuccess(allSuccess);
        writeAttributesResponse.setRecords(statusRecords);
        return doGeneralCommandResponse(request, writeAttributes);
    }
    
    private ZCLCommandPacket doConfigureReportingResponse(ZCLCommandPacket request) {
        ConfigureReporting configureReporting = new ConfigureReporting();
        try {
            configureReporting.drain(ZCLCommandPacketImpl.getPayloadAsFrameBuffer(request));
        } catch (Exception ex) {
            return DefaultResponse.doDefaultResponse(request, ZCLCluster.STATUS_MALFORMED_COMMAND);
        }
        ConfigureReporting.Record[] records = configureReporting.getRecords();
        int attributesLength = records.length;
        ZCLContext.debug("Configure Reporting Request: " + attributesLength + " Attributes");
        
        boolean allSuccess = true;
        ConfigureReportingResponse.StatusRecord[] statusRecords = new ConfigureReportingResponse.StatusRecord[attributesLength];
        for (int i = 0; i < attributesLength; i++) {
            final byte status;
            ConfigureReporting.Record record = records[i];
            byte direction = record.getDirection();
            if (direction == ZCLCluster.DIRECTION_INPUT) {
                ZCLAttribute attribute = cluster.getAttribute(record.getAttributeId());
                int min = record.getMinReportingInterval() & 0xFFFF;
                int max = record.getMaxReportingInterval() & 0xFFFF;
                if (attribute == null || !DataType.isReportableDataType(attribute.getDataType())) {
                    status = ZCLCluster.STATUS_UNSUPPORTED_ATTRIBUTE;
                } else if (!attribute.isReportable()) {
                    status = ZCLCluster.STATUS_UNREPORTABLE_ATTRIBUTE;
                } else if (record.getDataType() != attribute.getDataType()) {
                    status = ZCLCluster.STATUS_INVALID_DATA_TYPE;
                } else if (min < attribute.getMinimumReportingInterval() || (max != 0 && max < min)) {
                    status = ZCLCluster.STATUS_INVALID_VALUE;
                } else {
                    boolean registered;
                    try {
                        ReportingRegistration registration = new ReportingRegistration(
                                attribute.getAttributeId(),
                                min, max, record.getReportableChange());
                        registration.forceUpdateValue(attribute.getData());
                        registerReporting(registration);
                        registered = true;
                    } catch (IOException ex) {
                        // Attribute is offline.
                        registered = false;
                    }
                    status = registered ? ZCLCluster.STATUS_SUCCESS : ZCLCluster.STATUS_UNSUPPORTED_ATTRIBUTE;
                }
            } else if (direction == ZCLCluster.DIRECTION_OUTPUT) {
                RemoteAttributeCapability capability = cluster.getAttributeCapability(record.getAttributeId());
                if (capability == null ||
                    capability.getReportReceivingPolicy() == false ||
                    !capability.isReportTimeoutEnabled()) {
                    status = ZCLCluster.STATUS_UNSUPPORTED_ATTRIBUTE;
                } else {
                    // TODO: Configure report receiving...
                    status = ZCLCluster.STATUS_SUCCESS;
                }
            } else {
                // I can't find any convention in case the direction is malformed.
                status = ZCLCluster.STATUS_MALFORMED_COMMAND;
            }
            if (status != ZCLCluster.STATUS_SUCCESS) {
                allSuccess = false;
            }
            statusRecords[i].setStatus(status);
            statusRecords[i].setAttributeId(record.getAttributeId());
            statusRecords[i].setDirection(direction);
        }
        
        ConfigureReportingResponse configureReportingResponse = new ConfigureReportingResponse();
        configureReportingResponse.setAllSuccess(allSuccess);
        configureReportingResponse.setRecords(statusRecords);
        return doGeneralCommandResponse(request, configureReporting);
    }

    private ZCLCommandPacket doGeneralResponse(ZCLCommandPacket request) throws UnsupportedCommandException {
        byte commandId = request.getCommandId();
        ZCLCommand responseCommand = null;
        switch (commandId) {
        case ZCLCommand.READ_ATTRIBUTES:
            return doReadAttributesResponse(request);
        case ZCLCommand.WRITE_ATTRIBUTES:
        case ZCLCommand.WRITE_ATTRIBUTES_UNDIVIDED:
        case ZCLCommand.WRITE_ATTRIBUTES_NO_RSP:
            return doWriteAttributesResponse(request);
        case ZCLCommand.CONFIGURE_REPORTING:
            return doConfigureReportingResponse(request);
        case ZCLCommand.READ_ATTRIBUTES_RSP:
            responseCommand = new ReadAttributesResponse();
            break;
        case ZCLCommand.WRITE_ATTRIBUTES_RSP:
            responseCommand = new WriteAttributesResponse();
            break;
        case ZCLCommand.CONFIGURE_REPORTING_RSP:
            responseCommand = new ConfigureReportingResponse();
            break;
        case ZCLCommand.READ_REPORTING_CONFIGURATION_RSP:
            responseCommand = new ReadReportingConfigurationResponse();
            break;
        case ZCLCommand.DISCOVER_ATTRIBUTES_RSP:
            responseCommand = new DiscoverAttributesResponse();
            break;
        case ZCLCommand.WRITE_ATTRIBUTES_STRUCTURED_RSP:
            ZCLContext.debug("TODO: WriteAttributesStructuredRsp");
            break;
        case ZCLCommand.REPORT_ATTRIBUTES:
            responseCommand = new ReportAttributes();
            break;
        }
        if (responseCommand != null) {
            responseCommand.drain(ZCLCommandPacketImpl.getPayloadAsFrameBuffer(request));
            cluster.generalCommandResponseReceived(
                    request.getAddress(), request.getEndpoint(),
                    request.getTsn(), responseCommand);
            return DefaultResponse.doDefaultResponse(request, ZCLCluster.STATUS_SUCCESS);
        }
        throw new UnsupportedCommandException();
    }
    
    private ZCLCommandPacket doResponse(ZCLCommandPacket commandPacket) {
        if (DefaultResponse.isDefaultResponse(commandPacket)) {
            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.drain(ZCLCommandPacketImpl.getPayloadAsFrameBuffer(commandPacket));
            cluster.generalCommandResponseReceived(
                    commandPacket.getAddress(), commandPacket.getEndpoint(),
                    commandPacket.getTsn(), defaultResponse);
            // There is no related response for default rsp.
            return null;
        }
        
        ZCLCommandPacket response;
        
        ZCLCommandAuthenticator authenticator = getZCLCommandAuthenticator();
        if (authenticator != null && !authenticator.authenticate(commandPacket)) {
            ZCLContext.debug("ZCL command rejected by authenticator.");
            response = DefaultResponse.doDefaultResponse(commandPacket, ZCLCluster.STATUS_NOT_AUTHORIZED);
        } else {
            try {
                if (commandPacket.isClusterSpecific() || isPassthroughGeneralCommands()) {
                    response = cluster.doResponse(commandPacket);
                } else {
                    try {
                        response = doGeneralResponse(commandPacket);
                    } catch (UnsupportedCommandException ex) {
                        // Unsupported general commands may be handled by cluster.
                        response = cluster.doResponse(commandPacket);
                    }
                }
            } catch (UnsupportedCommandException ex) {
                // Not Supported Resp
                byte errorCode;
                if (commandPacket.isManufacturerSpecific()) {
                    errorCode = commandPacket.isClusterSpecific() ?
                            ZCLCluster.STATUS_UNSUP_MANUF_CLUSTER_COMMAND :
                            ZCLCluster.STATUS_UNSUP_MANUF_GENERAL_COMMAND;
                } else {
                    errorCode = commandPacket.isClusterSpecific() ?
                            ZCLCluster.STATUS_UNSUP_CLUSTER_COMMAND :
                            ZCLCluster.STATUS_UNSUP_GENERAL_COMMAND;
                }

                response = DefaultResponse.doDefaultResponse(commandPacket, errorCode);
            } catch (Exception ex) {
                ZCLContext.error("Uncaught exception in response", ex);
                response = DefaultResponse.doDefaultResponse(commandPacket, ZCLCluster.STATUS_SOFTWARE_FAILURE);
            }
        }
        
        return response;
    }
    
    public ZCLCommandPacket doResponse(ZCLCommandPacket commandPacket, boolean unicast) {
        // Perform response on the related cluster.
        ZCLCommandPacket response = doResponse(commandPacket);
        if (response == null) {
            return null;
        }

        final boolean sendResponse;
        if (DefaultResponse.isDefaultResponse(response)) {
            // Check the condition.
            byte status = response.getPayload()[response.getPayloadOffset() + 1];
            if (unicast && (!commandPacket.isDisableDefaultResponse() || (status != ZCLCluster.STATUS_SUCCESS))) {
                sendResponse = true;
            } else {
                sendResponse = false;
            }
        } else {
            // Non-DefaultResponses are always send to remote cluster. 
            sendResponse = true;
        }
        return sendResponse ? response : null;
    }
    
    public ZCLCommand doReportAttributes() {
        ReportAttributes.AttributeReport[] reports = prepareReports();
        if (reports == null || reports.length == 0) {
            return null;
        }
        
        ReportAttributes reportAttributes = new ReportAttributes();
        reportAttributes.setReports(reports);
        return reportAttributes;
    }
    
    private void registerReporting(ReportingRegistration registration) {
        if (reportingSet.contains(registration)) {
            reportingSet.remove(registration);
        }
        if (registration.getMaximumReportingInterval() == 0xFFFF) {
            return;
        }
        reportingSet.add(registration);
    }
    
    private ReportAttributes.AttributeReport[] prepareReports() {
        ArrayList reportList = new ArrayList();
        for (Iterator it = reportingSet.iterator(); it.hasNext(); ) {
            ReportingRegistration registration = (ReportingRegistration) it.next();
            try {
                int elapsedTime = registration.getCurrentElapsedTime();
                if (elapsedTime < registration.getMinimumReportingInterval()) {
                    continue;
                }

                ZCLAttribute attribute = cluster.getAttribute(registration.getAttributeId());
                if (attribute == null) {
                    // Attribute is offline, we need to remove the registration.
                    reportingSet.remove(registration);
                    // Move on to the next registration.
                    continue;
                }
                
                DataType dataType = DataType.getDataType(attribute.getDataType());
                Object data = null;
                boolean updated = false;
                if (registration.getMaximumReportingInterval() == 0) {
                    // Periodic reporting is disabled, update only if there is any change.
                    data = attribute.getData();
                    updated = registration.updateValue(dataType, data);
                    if (updated) {
                        registration.initCurrentInterval();
                    }
                } else {
                    boolean overTime = registration.getMaximumReportingInterval() <= elapsedTime;
                    if (!overTime) {
                        if (!registration.isUpdatedInCurrentInterval()) {
                            data = attribute.getData();
                            updated = registration.updateValue(dataType, data);
                        }
                    } else {
                        if (!registration.isUpdatedInCurrentInterval()) {
                            registration.forceUpdateValue(data);
                        }
                        registration.initCurrentInterval();
                    }
                }
                if (updated) {
                    ReportAttributes.AttributeReport report = new ReportAttributes.AttributeReport();
                    report.setAttributeId(attribute.getAttributeId());
                    report.setDataType(attribute.getDataType());
                    report.setData(data);
                    reportList.add(report);
                }
            } catch (IOException ex) {
                // Skip
            }
        }
        if (reportList.size() > 0) {
            return (ReportAttributes.AttributeReport[])
                reportList.toArray(new ReportAttributes.AttributeReport[0]);
        } else {
            return null;
        }
    }
}
