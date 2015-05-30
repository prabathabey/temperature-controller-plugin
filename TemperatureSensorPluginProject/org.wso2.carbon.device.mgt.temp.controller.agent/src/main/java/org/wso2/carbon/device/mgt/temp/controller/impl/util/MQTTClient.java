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
package org.wso2.carbon.device.mgt.temp.controller.impl.util;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.util.concurrent.Semaphore;

/**
 * Created by deep on 4/29/14.
 */
public class MQTTClient implements MqttCallback {

    //private static final Log log = LogFactory.getLog(MQTTListener.class);

    private MqttClient mqttClient;
    private MqttConnectOptions connectionOptions;
    private boolean cleanSession;
    private String brokerUrl;
    private String password;
    private String userName;
    private String mqttClientId;
    private Semaphore semaphore = new Semaphore(0);
    public MQTTClient(MQTTBrokerConnectionConfig mqttBrokerConnectionConfig, String mqttClientId) {
        //Initializing the variables locally
        this.brokerUrl = mqttBrokerConnectionConfig.getBrokerUrl();
        this.mqttClientId = mqttClientId;
        // this.quietMode = quietMode;
        this.cleanSession = mqttBrokerConnectionConfig.isCleanSession();
        this.password = mqttBrokerConnectionConfig.getBrokerPassword();
        this.userName = mqttBrokerConnectionConfig.getBrokerUsername();

        //Sotring messages until the server fetches them
        String temp_directory = System.getProperty("java.io.tmpdir");
        MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(temp_directory);


        try {
            // Construct the connection options object that contains connection parameters
            // such as cleanSession and LWT
            connectionOptions = new MqttConnectOptions();
            connectionOptions.setCleanSession(cleanSession);
            if (password != null) {
                connectionOptions.setPassword(this.password.toCharArray());
            }
            if (userName != null) {
                connectionOptions.setUserName(this.userName);
            }

            // Construct an MQTT blocking mode client
            mqttClient = new MqttClient(this.brokerUrl, mqttClientId, dataStore);

            // Set this wrapper as the callback handler
            mqttClient.setCallback(this);
		// Connect to the MQTT server
        mqttClient.connect(connectionOptions);
          mqttClient.subscribe("iot/demo");
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
    public void publish(String topicName, int qos, byte[] payload) throws MqttException {

        // Connect to the MQTT server
        //mqttClient.connect(connectionOptions);

        // Create and configure a message
        MqttMessage message = new MqttMessage(payload);
        message.setQos(qos);

        // Send the message to the server, control is not returned until
        // it has been delivered to the server meeting the specified
        // quality of service.
        mqttClient.publish(topicName, message);
    }


    public void connectionLost(Throwable throwable) {

    }

    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {


    }

    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
