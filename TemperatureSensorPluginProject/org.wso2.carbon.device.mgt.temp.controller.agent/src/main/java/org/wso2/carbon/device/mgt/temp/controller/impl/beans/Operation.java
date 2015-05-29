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

package org.wso2.carbon.device.mgt.temp.controller.impl.beans;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.wso2.carbon.device.mgt.temp.controller.impl.exception.OperationConfigurationException;

import java.io.IOException;

/*
* This abstract class is used for extending generic functions with regard to operation.
*/
public abstract class Operation {

	/*
	* This method is used to convert operation object to a json format.
	* 
	* @return json formatted String.
	*/
	public String toJSON() throws OperationConfigurationException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(this);
		} catch (JsonMappingException e) {
			throw new OperationConfigurationException("Error generating JSON representation", e);
		} catch (JsonGenerationException e) {
			throw new OperationConfigurationException("Error generating JSON representation", e);
		} catch (IOException e) {
			throw new OperationConfigurationException("Error generating JSON representation", e);
		}
	}
}
