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
package org.wso2.carbon.device.mgt.temp.controller.impl.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.spi.DeviceMgtService;
import org.wso2.carbon.device.mgt.core.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.temp.controller.impl.TCDeviceManagerImpl;
import org.wso2.carbon.device.mgt.temp.controller.impl.TCSchemaInitializer;
import org.wso2.carbon.device.mgt.temp.controller.impl.TCUtil;
import org.wso2.carbon.device.mgt.temp.controller.impl.config.TCConfig;
import org.wso2.carbon.device.mgt.temp.controller.impl.dao.TCDAOFactory;
import org.wso2.carbon.ndatasource.core.DataSourceService;


/**
 * @scr.component name="org.wso2.carbon.device.mgt.temp.controller.impl.internal.TCServiceComponent"
 * immediate="true"
 * @scr.reference name="org.wso2.carbon.ndatasource"
 * interface="org.wso2.carbon.ndatasource.core.DataSourceService"
 * cardinality="1..1"
 * policy="dynamic"
 * bind="setDataSourceService"
 * unbind="unsetDataSourceService"
 * <p/>
 */
public class TCServiceComponent {

    private ServiceRegistration tcDeviceMgtServiceRef;
    private static final Log log = LogFactory.getLog(TCServiceComponent.class);

    protected void activate(ComponentContext ctx) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Activating Mobile Device Management Service Component");
        }
        try {
            BundleContext bundleContext = ctx.getBundleContext();
            /* Initializing Temperature Controller Configuration */
            TCConfig.init();
            /* Initializing Temperature Controller DAO Factory */
            TCDAOFactory.init(TCUtil.resolveDataSource(TCConfig.getInstance().getDsConfig()));

            /* If -Dsetup option enabled then create device management database schema */
            String setupOption =
                    System.getProperty(DeviceManagementConstants.Common.PROPERTY_SETUP);
            if (setupOption != null) {
                if (log.isDebugEnabled()) {
                    log.debug("-Dsetup is enabled. Device management repository schema initialization is about to " +
                            "begin");
                }
                this.setupDeviceManagementSchema(TCConfig.getInstance().getDsConfig());
            }

            tcDeviceMgtServiceRef =
                    bundleContext.registerService(DeviceMgtService.class.getName(), new TCDeviceManagerImpl(), null);

            if (log.isDebugEnabled()) {
                log.debug("Temperature Controller Device Management Service Component has been successfully activated");
            }
        } catch (Throwable e) {
            log.error("Error occurred while activating Temperature Controller Device Management Service Component", e);
        }
    }

    public void deactivate(ComponentContext ctx) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("De-activating Temperature Controller Device Management Service Component");
        }
        try {
            if (tcDeviceMgtServiceRef != null) {
                tcDeviceMgtServiceRef.unregister();
            }
            if (log.isDebugEnabled()) {
                log.debug("Temperature Controller Device Management Service Component has been successfully " +
                        "de-activated");
            }
        } catch (Throwable e) {
            log.error("Error occurred while de-activating Temperature Controller Device Management bundle", e);
        }
    }

    private void setupDeviceManagementSchema(TCConfig.DataSourceConfig config)
            throws DeviceManagementException {
        TCSchemaInitializer initializer = new TCSchemaInitializer(config);
        log.info("Initializing device management repository database schema");

        try {
            initializer.createRegistryDatabase();
        } catch (Exception e) {
            throw new DeviceManagementException(
                    "Error occurred while initializing Device Management database schema", e);
        }
        if (log.isDebugEnabled()) {
            log.debug("Device management metadata repository schema has been successfully initialized");
        }
    }

    protected void setDataSourceService(DataSourceService dataSourceService) {
        /* This is to avoid mobile device management component getting initialized before the underlying datasources
        are registered */
        if (log.isDebugEnabled()) {
            log.debug("Data source service set to mobile service component");
        }
    }

    protected void unsetDataSourceService(DataSourceService dataSourceService) {
        //do nothing
    }

}
