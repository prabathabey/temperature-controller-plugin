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
package org.wso2.carbon.device.mgt.temp.controller.impl.config;

import org.w3c.dom.Document;
import org.wso2.carbon.device.mgt.temp.controller.impl.TCConfigurationException;
import org.wso2.carbon.device.mgt.temp.controller.impl.TCUtil;
import org.wso2.carbon.utils.CarbonUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;

@XmlRootElement(name = "TemperatureControllerConfig")
public class TCConfig {

    private static final String TC_CONFIG_PATH =
            CarbonUtils.getEtcCarbonConfigDirPath() + File.separator + "tc-config.xml";
    private DataSourceConfig dsConfig;

    @XmlElement(name = "MqttBroker", required = true)
    public MqttBrokerConfig getMqttBrokerConfig() {
        return mqttBrokerConfig;
    }

    public void setMqttBrokerConfig(MqttBrokerConfig mqttBrokerConfig) {
        this.mqttBrokerConfig = mqttBrokerConfig;
    }

    private MqttBrokerConfig mqttBrokerConfig;
    private static TCConfig config = new TCConfig();

    private TCConfig() {
    }

    public static TCConfig getInstance() {
        if (config == null) {
            throw new RuntimeException("Temperature Controller Configuration is not " +
                                       "initialized properly");
        }
        return config;
    }

    @XmlElement(name = "DataSource", required = true)
    public DataSourceConfig getDsConfig() {
        return dsConfig;
    }

    public void setDsConfig(DataSourceConfig dsConfig) {
        this.dsConfig = dsConfig;
    }

    @XmlRootElement(name = "DataSource")
    public static class DataSourceConfig {
        private String jndiLookupName;

        public DataSourceConfig() {}

        @XmlElement(name = "JNDILookupName", required = true)
        public String getJndiLookupName() {
            return jndiLookupName;
        }

        public void setJndiLookupName(String jndiLookupName) {
            this.jndiLookupName = jndiLookupName;
        }
    }

    @XmlRootElement(name = "MqttBroker")
    public static class MqttBrokerConfig {
        private String host;
        private String port;

        public MqttBrokerConfig() {}

        @XmlElement(name = "host", required = true)
        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        @XmlElement(name = "port", required = true)
        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }
    }

    public static void init() throws TCConfigurationException {
        try {
            File authConfig = new File(TCConfig.TC_CONFIG_PATH);
            Document doc = TCUtil.convertToDocument(authConfig);

            /* Un-marshaling Webapp Authenticator configuration */
            JAXBContext ctx = JAXBContext.newInstance(TCConfig.class);
            Unmarshaller unmarshaller = ctx.createUnmarshaller();
            //unmarshaller.setSchema(getSchema());
            config = (TCConfig) unmarshaller.unmarshal(doc);
        } catch (JAXBException e) {
            throw new TCConfigurationException("Error occurred while un-marshalling Temperature Controller " +
                    "Configuration Framework Config", e);
        }
    }

}
