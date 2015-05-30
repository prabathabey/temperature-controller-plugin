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

/**
 * Created by deep on 4/29/14.
 */
public class MQTTBrokerConnectionConfig {
    private String brokerProtocole = "tcp";
    private String brokerHost = null;
    private String brokerPort = null;
    private String brokerUsername = null;
    private String brokerPassword = null;
    private boolean cleanSession = false;
    private String brokerUrl;

    public MQTTBrokerConnectionConfig(String host, String port){
        this.brokerHost = host;
        this.brokerPort = port;
    }

    public String getProkerPort() {
        return brokerPort;
    }

    public void setProkerPort(String prokerPort) {
        this.brokerPort = prokerPort;
    }

    public String getBrokerProtocole() {
        return brokerProtocole;
    }

    public void setBrokerProtocole(String brokerProtocole) {
        this.brokerProtocole = brokerProtocole;
    }

    public String getBrokerHost() {
        return brokerHost;
    }

    public void setBrokerHost(String brokerHost) {
        this.brokerHost = brokerHost;
    }

    public String getBrokerPassword() {
        return brokerPassword;
    }

    public void setBrokerPassword(String brokerPassword) {
        this.brokerPassword = brokerPassword;
    }

    public String getBrokerUsername() {
        return brokerUsername;
    }

    public void setBrokerUsername(String brokerUsername) {
        this.brokerUsername = brokerUsername;
    }

    public void setCleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
    }


    public boolean isCleanSession() {
        return cleanSession;
    }

    public String getBrokerUrl() {
        return new String(this.getBrokerProtocole() + "://" + this.getBrokerHost() + ":" + this.getProkerPort());
    }
}
