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
package org.wso2.carbon.device.mgt.temp.controller.impl.dao;

import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TCDAO {

    private DataSource dataSource;

    public TCDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean addDevice(Device device) throws TCDAOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement("INSERT INTO TC_DEVICE(" +
                    "VERSION, SYSTEM, MACHINE, BUILD_NAME, NODE, COMPILER, KERNEL, PLATFORM, BUILD_DATE, " +
                    "PLATFORM_NAME) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            Map<String, String> properties = this.listToMap(device.getProperties());
            stmt.setString(1, properties.get("Version"));
            stmt.setString(2, properties.get("System"));
            stmt.setString(3, properties.get("Machine"));
            stmt.setString(6, properties.get("Python Compiler"));
            stmt.setString(4, properties.get("Python Build Name"));
            stmt.setString(9, properties.get("Python Build Date"));
            stmt.setString(5, properties.get("Node"));
            stmt.setString(7, properties.get("Kernel Name"));
            stmt.setString(8, properties.get("Platform"));
            stmt.setString(10, properties.get("Platform Name"));

            stmt.execute();
        } catch (SQLException e) {
            throw new TCDAOException("Error occurred while adding temperature controller", e);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ignore) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignore) {
                }
            }
        }
        return true;
    }

    public Device getDevice(DeviceIdentifier deviceId) throws TCDAOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Device device = new Device();
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement("SELECT ID, DEVICE_IDENTIFIER, VERSION, SYSTEM, MACHINE, BUILD_NAME, NODE, COMPILER, KERNEL, " +
                    "PLATFORM, BUILD_DATE, PLATFORM_NAME FROM TC_DEVICE WHERE DEVICE_IDENTIFIER = ?");
            stmt.setString(1, deviceId.getId());

            rs = stmt.executeQuery();
            if (rs.next()) {
                List<Device.Property> properties = new ArrayList<Device.Property>();
                properties.add(this.getProperty("version", rs.getString("VERSION")));
                properties.add(this.getProperty("System", rs.getString("SYSTEM")));
                properties.add(this.getProperty("Machine", rs.getString("MACHINE")));
                properties.add(this.getProperty("Python Build Name", rs.getString("BUILD_NAME")));
                properties.add(this.getProperty("Node", rs.getString("NODE")));
                properties.add(this.getProperty("Python Compiler", rs.getString("COMPILER")));
                properties.add(this.getProperty("Kernel Name", rs.getString("KERNEL")));
                properties.add(this.getProperty("platform", rs.getString("PLATFORM")));
                properties.add(this.getProperty("Python Build Date", rs.getString("BUILD_DATE")));
                properties.add(this.getProperty("Platform Name", rs.getString("PLATFORM_NAME")));
                device.setId(rs.getInt("ID"));
                device.setDeviceIdentifier("DEVICE_IDENTIFIER");
                device.setProperties(properties);
            }
            return device;
        } catch (SQLException e) {
            throw new TCDAOException("Error occurred while adding temperature controller", e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignore) {
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ignore) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignore) {
                }
            }
        }
    }

    private Device.Property getProperty(String name, String value) {
        Device.Property property = new Device.Property();
        property.setName(name);
        property.setValue(value);
        return property;
    }

    private Map<String, String> listToMap(List<Device.Property> properties) {
        Map<String, String> map = new HashMap<String, String>();
        for (Device.Property property : properties) {
            map.put(property.getName(), property.getValue());
        }
        return map;
    }

}
