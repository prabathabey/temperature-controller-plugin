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
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.simple.JSONObject;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.device.mgt.core.operation.mgt.CommandOperation;
import org.wso2.carbon.device.mgt.temp.controller.impl.beans.Buzzer;
import org.wso2.carbon.device.mgt.temp.controller.impl.beans.wrapper.BuzzerBeanWrapper;
import org.wso2.carbon.device.mgt.temp.controller.impl.exception.TCOperationException;
import org.wso2.carbon.device.mgt.temp.controller.impl.util.AgentUtil;
import org.wso2.carbon.device.mgt.temp.controller.impl.util.MQTTBrokerConnectionConfig;
import org.wso2.carbon.device.mgt.temp.controller.impl.util.MQTTClient;
import org.wso2.carbon.device.mgt.temp.controller.impl.util.Message;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OperationManagementService {
	private static Log log = LogFactory.getLog(OperationManagementService.class);
	//TODO: Move this to constants
	public static final String BUZZER = "BUZZER";
	public static final String MQTT_AGENT_HOSTNAME = "192.168.0.19";
	private static MQTTClient mqttClient;
	static{
		log.info("Connecting to mqtt host " + MQTT_AGENT_HOSTNAME);
		MQTTBrokerConnectionConfig mqttBrokerConnectionConfig =
				new MQTTBrokerConnectionConfig(MQTT_AGENT_HOSTNAME, "1883");
		String clientId = "DeviceManager";
		mqttClient = new MQTTClient(mqttBrokerConnectionConfig, clientId);
	}

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
			List<String> deviceIDs = buzzerBeanWrapper.getDeviceIDs();
			for (int i = 0; i < deviceIDs.size(); i++) {
				String id = deviceIDs.get(i);
				String topicName = "devices/" + id;

				JSONObject obj = new JSONObject();
				JSONObject subObj = new JSONObject();
				subObj.put("enabled", buzzerBeanWrapper.getOperation().isEnabled());
				obj.put("operation","BUZZER");
				obj.put("properties", subObj);
				log.info(obj.toJSONString());
				mqttClient.publish(topicName, 1, obj.toString().getBytes());
			}

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
		} catch (MqttException e) {
			String errorMessage = "Issue in send MQTT message";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new TCOperationException(message, responseMediaType);
		}
	}
}
