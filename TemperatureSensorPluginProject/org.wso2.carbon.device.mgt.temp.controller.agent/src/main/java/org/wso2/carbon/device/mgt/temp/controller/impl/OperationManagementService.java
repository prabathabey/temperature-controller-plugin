/*
 * *
 *  *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *
 */

package org.wso2.carbon.device.mgt.temp.controller.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.device.mgt.core.operation.mgt.CommandOperation;
import org.wso2.carbon.device.mgt.temp.controller.impl.beans.Buzzer;
import org.wso2.carbon.device.mgt.temp.controller.impl.beans.wrapper.BuzzerBeanWrapper;
import org.wso2.carbon.device.mgt.temp.controller.impl.exception.TCOperationException;
import org.wso2.carbon.device.mgt.temp.controller.impl.util.AgentUtil;
import org.wso2.carbon.device.mgt.temp.controller.impl.util.Message;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OperationManagementService {
	private static Log log = LogFactory.getLog(OperationManagementService.class);
	//TODO: Move this to constants
	public static final String BUZZER = "BUZZER";
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("buzzer")
	public Response configureBuzzer(@HeaderParam("Accept") String acceptHeader,
	                             BuzzerBeanWrapper buzzerBeanWrapper) {
		if (log.isDebugEnabled()) {
			log.debug("Invoking Android Camera operation");
		}

		MediaType responseMediaType = AgentUtil.getResponseMediaType(acceptHeader);
		Message message = new Message();

		try {
			Buzzer camera = buzzerBeanWrapper.getOperation();

			if (camera == null) {
				throw new OperationManagementException("Buzzer bean is empty");
			}

			CommandOperation operation = new CommandOperation();
			operation.setCode(BUZZER);
			operation.setType(Operation.Type.COMMAND);
			operation.setEnabled(camera.isEnabled());

			return AgentUtil.getOperationResponse(buzzerBeanWrapper.getDeviceIDs(), operation, message,
			                                            responseMediaType);

		} catch (OperationManagementException e) {
			String errorMessage = "Issue in retrieving operation management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new TCOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			String errorMessage = "Issue in retrieving device management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new TCOperationException(message, responseMediaType);
		}
	}
}
