/*
 *   Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.wso2.carbon.device.mgt.temp.controller.impl.util;

import org.apache.commons.lang.StringUtils;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementService;
import org.wso2.carbon.device.mgt.temp.controller.impl.exception.BadRequestException;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class AgentUtil {
	public static final String DEVICE_ID_NOT_FOUND = "Device Id not found for device found at %s";
	public static final String DEVICE_ID_SERVICE_NOT_FOUND =
			"Issue in retrieving device management service instance for device found at %s";
	public static final int MULTI_STATUS_HTTP_CODE = 207;
	private static final String COMMA_SEPARATION_PATTERN = ", ";
	private static final String PLUGIN_TEMP_SENSOR = "TemperatureController";

	public static DeviceManagementService getDeviceManagementService() {

		//TODO: complete login change super tenent context
		DeviceManagementService dmService;
		try {
			PrivilegedCarbonContext.startTenantFlow();
			PrivilegedCarbonContext.getThreadLocalCarbonContext()
			                       .setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
			PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(MultitenantConstants.SUPER_TENANT_ID);
			dmService = (DeviceManagementService) PrivilegedCarbonContext.getThreadLocalCarbonContext()
			                                                             .getOSGiService(DeviceManagementService.class,
			                                                                             null);
		} finally {
			PrivilegedCarbonContext.endTenantFlow();
		}
		return dmService;
	}

	public static DeviceIdentifier convertToDeviceIdentifierObject(String deviceId) {
		DeviceIdentifier identifier = new DeviceIdentifier();
		identifier.setId(deviceId);
		identifier.setType(PLUGIN_TEMP_SENSOR);
		return identifier;
	}

	public static DeviceIDHolder validateDeviceIdentifiers(List<String> deviceIDs, Message message,
	                                                MediaType responseMediaType) {

		if (deviceIDs == null) {
			message.setResponseMessage("Device identifier list is empty");
			throw new BadRequestException(message, responseMediaType);
		}

		List<String> errorDeviceIdList = new ArrayList<String>();
		List<DeviceIdentifier> validDeviceIDList = new ArrayList<DeviceIdentifier>();

		int deviceIDCounter = 0;
		for (String deviceID : deviceIDs) {

			deviceIDCounter++;

			if (deviceID == null || deviceID.isEmpty()) {
				errorDeviceIdList.add(String.format(DEVICE_ID_NOT_FOUND, deviceIDCounter));
				continue;
			}

			try {
				DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
				deviceIdentifier.setId(deviceID);
				deviceIdentifier.setType(PLUGIN_TEMP_SENSOR);

				Device device = getDeviceManagementService().
						getDevice(deviceIdentifier);

				if (device == null || device.getDeviceIdentifier() == null ||
				    device.getDeviceIdentifier().isEmpty()) {
					errorDeviceIdList
							.add(String.format(DEVICE_ID_NOT_FOUND, deviceIDCounter));
					continue;
				}

				validDeviceIDList.add(deviceIdentifier);
			} catch (DeviceManagementException e) {
				errorDeviceIdList.add(String.format(DEVICE_ID_SERVICE_NOT_FOUND,
				                                    deviceIDCounter));
			}
		}

		DeviceIDHolder deviceIDHolder = new DeviceIDHolder();
		deviceIDHolder.setValidDeviceIDList(validDeviceIDList);
		deviceIDHolder.setErrorDeviceIdList(errorDeviceIdList);

		return deviceIDHolder;
	}

	public static Response getOperationResponse(List<String> deviceIDs, Operation operation, Message message,
	                                            MediaType responseMediaType)
			throws DeviceManagementException, OperationManagementException {
		DeviceIDHolder deviceIDHolder = validateDeviceIdentifiers(deviceIDs, message, responseMediaType);

		getDeviceManagementService().addOperation(operation, deviceIDHolder.getValidDeviceIDList());

		if (!deviceIDHolder.getErrorDeviceIdList().isEmpty()) {
			return javax.ws.rs.core.Response.status(MULTI_STATUS_HTTP_CODE).type(responseMediaType)
			                                .entity(
					                                                   convertErrorMapIntoErrorMessage(
							                                                   deviceIDHolder.getErrorDeviceIdList()))
			                                .build();
		}

		return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.CREATED).
				type(responseMediaType).build();
	}
	public static String convertErrorMapIntoErrorMessage(List<String> errorDeviceIdList) {
		return StringUtils.join(errorDeviceIdList.iterator(), COMMA_SEPARATION_PATTERN);
	}
	public static MediaType getResponseMediaType(String acceptHeader) {

		MediaType responseMediaType;
		if (MediaType.WILDCARD.equals(acceptHeader)) {
			responseMediaType = MediaType.APPLICATION_JSON_TYPE;
		} else {
			responseMediaType = MediaType.valueOf(acceptHeader);
		}

		return responseMediaType;
	}
}
