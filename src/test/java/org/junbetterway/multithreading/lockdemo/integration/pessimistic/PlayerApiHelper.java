/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.junbetterway.multithreading.lockdemo.integration.pessimistic;

import java.math.BigDecimal;
import java.util.HashMap;

import org.junbetterway.multithreading.lockdemo.core.BaseApiHelper;
import org.junbetterway.multithreading.lockdemo.core.IntegrationUtils;
import org.junbetterway.multithreading.lockdemo.pessimistic.Player;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

/**
 * 
 * @author JunMinon
 *
 */
public class PlayerApiHelper extends BaseApiHelper {

	private final static String BASE_API = "/api/player";
	
	private final ObjectMapper mapper = new ObjectMapper();

	public PlayerApiHelper(RequestSpecification requestSpec, ResponseSpecification responseSpec) {
		super(requestSpec, responseSpec);
	}
	
	public Integer create(final Player player) throws NumberFormatException, JsonProcessingException {
		
		return Integer.valueOf(IntegrationUtils.performServerPost(requestSpec, responseSpec, 
				BASE_API, mapper.writeValueAsString(player), null));
	
	}
	
	public HashMap<String, Object> bet(final int playerId, final BigDecimal betAmount) throws NumberFormatException, JsonProcessingException {

		String betUrl = BASE_API + "/"+ playerId + "/bet";
		Player player = Player.builder().betAmount(betAmount).build();
		return IntegrationUtils.performServerPost(requestSpec, responseSpec, betUrl, mapper.writeValueAsString(player), "");

	}
	
	public HashMap<String, Object> get(final int playerId) throws NumberFormatException, JsonProcessingException {

		String getURL = BASE_API + "/"  + playerId;
		
		return IntegrationUtils.performServerGet(requestSpec, responseSpec, getURL, "");

	}
	
}
