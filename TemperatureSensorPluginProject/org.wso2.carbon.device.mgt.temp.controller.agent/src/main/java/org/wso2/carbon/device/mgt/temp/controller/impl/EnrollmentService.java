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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EnrollmentService {

    private static Log log = LogFactory.getLog(EnrollmentService.class);

    @POST
    @Path("/enroll")
    public Response enrollDevice(org.wso2.carbon.device.mgt.common.Device device) {
        String msg;
        try {
            device.setType("TemperatureController");
            AgentUtil.getDeviceManagementService().enrollDevice(device);

            return Response.status(Response.Status.ACCEPTED).entity("Device enrollment succeeded").build();
        } catch (DeviceManagementException e) {
            msg = "Error occurred while enrolling the device";
            log.error(msg, e);
            return Response.serverError().entity(msg).build();
        }
    }

//    @POST
//    @Path("{sendMail}")
//    public void sendMail() throws TCException {
//        String msg;
//        try {
//            EmailMessageProperties messageProperties = new EmailMessageProperties();
//            messageProperties.setMailTo(new String[]{"manojg@wso2.com"});
//            messageProperties.setFirstName("Manoj");
//            messageProperties.setTitle("Mr");
//            messageProperties.setUserName("ManojG");
//            messageProperties.setPassword("welcome");
//
//            AgentUtil.getDeviceManagementService().sendRegistrationEmail(messageProperties);
//        } catch (DeviceManagementException e) {
//            msg = "Error occurred while enrolling the device";
//            log.error(msg, e);
//            throw new TCException(msg, e);
//        }
//    }

    @GET
    @Path("{id}")
    public Response isEnrolled(@PathParam("id") String id) throws TCException {
        String msg;
        boolean result;
        DeviceIdentifier deviceIdentifier = AgentUtil.convertToDeviceIdentifierObject(id);

        try {
            result = AgentUtil.getDeviceManagementService().isEnrolled(deviceIdentifier);
            if (result) {
                return Response.ok("Device has already been enrolled").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Device not found").build();
            }
        } catch (DeviceManagementException e) {
            msg = "Error occurred while enrollment of the device.";
            log.error(msg, e);
            throw new TCException(msg, e);
        }
    }

    @PUT
    @Path("{id}")
    public Response modifyEnrollment(@PathParam("id") String id,
                                    org.wso2.carbon.device.mgt.common.Device device)
            throws TCException {
        String msg;
        boolean result;
        try {
            device.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
            result = AgentUtil.getDeviceManagementService().modifyEnrollment(device);
            if (result) {
                return Response.ok("Device enrollment has been updated successfully").build();
            } else {
                return Response.status(Response.Status.NOT_MODIFIED).entity("Device not found for enrollment").build();
            }
        } catch (DeviceManagementException e) {
            msg = "Error occurred while modifying enrollment of the device";
            log.error(msg, e);
            throw new TCException(msg, e);
        }
    }

    @DELETE
    @Path("{id}")
    public Response disenrollDevice(@PathParam("id") String id) throws TCException {
        boolean result;
        String msg;
        DeviceIdentifier deviceIdentifier = AgentUtil.convertToDeviceIdentifierObject(id);

        try {
            result = AgentUtil.getDeviceManagementService().disenrollDevice(deviceIdentifier);
            if (result) {
                return Response.ok("Device has removed successfully").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Device Not Found").build();
            }
        } catch (DeviceManagementException e) {
            msg = "Error occurred while dis-enrolling the device";
            log.error(msg, e);
            throw new TCException(msg, e);
        }
    }

}

