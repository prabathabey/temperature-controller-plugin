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
package org.wso2.carbon.device.mgt.temp.controller.impl;

import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.FeatureManager;
import org.wso2.carbon.device.mgt.common.app.mgt.AppManagerConnectorException;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.spi.DeviceMgtService;
import org.wso2.carbon.device.mgt.temp.controller.impl.dao.TCDAO;
import org.wso2.carbon.device.mgt.temp.controller.impl.dao.TCDAOException;
import org.wso2.carbon.device.mgt.temp.controller.impl.dao.TCDAOFactory;

import java.util.List;

public class TCDeviceManagerImpl implements DeviceMgtService {
    
    private static final String PLUGIN_TEMP_SENSOR = "TemperatureController";
    private TCDAO dao = TCDAOFactory.getTempControllerDAO();
    
    @Override
    public String getProviderType() {
        return TCDeviceManagerImpl.PLUGIN_TEMP_SENSOR;
    }

    @Override
    public FeatureManager getFeatureManager() {
        return null;
    }

    @Override
    public boolean enrollDevice(Device device) throws DeviceManagementException {
        try {
            return dao.addDevice(device);
        } catch (TCDAOException e) {
            throw new DeviceManagementException("Error occurred while enrolling temperature controller", e);
        }
    }

    @Override
    public boolean modifyEnrollment(Device device) throws DeviceManagementException {
        return false;  
    }

    @Override
    public boolean disenrollDevice(DeviceIdentifier deviceIdentifier) throws DeviceManagementException {
        return false;  
    }

    @Override
    public boolean isEnrolled(DeviceIdentifier deviceIdentifier) throws DeviceManagementException {
        return false;  
    }

    @Override
    public boolean isActive(DeviceIdentifier deviceIdentifier) throws DeviceManagementException {
        return false;  
    }

    @Override
    public boolean setActive(DeviceIdentifier deviceIdentifier, boolean b) throws DeviceManagementException {
        return false;  
    }

    @Override
    public List<Device> getAllDevices() throws DeviceManagementException {
        return null;  
    }

    @Override
    public Device getDevice(DeviceIdentifier deviceIdentifier) throws DeviceManagementException {
        try {
            return dao.getDevice(deviceIdentifier);
        } catch (TCDAOException e) {
            throw new DeviceManagementException("Error occurred while retrieving information of the device '" +
                    deviceIdentifier.getId() + "'", e);
        }
    }

    @Override
    public boolean updateDeviceInfo(DeviceIdentifier deviceIdentifier, Device device) throws DeviceManagementException {
        return false;  
    }

    @Override
    public boolean setOwnership(DeviceIdentifier deviceIdentifier, String s) throws DeviceManagementException {
        return false;  
    }

    @Override
    public void installApplication(Operation operation,
                                   List<DeviceIdentifier> deviceIdentifiers) throws AppManagerConnectorException {
    }

}
