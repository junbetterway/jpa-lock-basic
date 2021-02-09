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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junbetterway.multithreading.lockdemo.pessimistic.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

/**
 * 
 * @author JunMinon
 *
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class PlayerPessimisticLockingTestIT {

	private static PlayerApiHelper playerApiHelper;
	
	@BeforeAll
	public static void setup() {
		
    	RequestSpecification requestSpec = new RequestSpecBuilder()
        		.setContentType(ContentType.JSON)
        		.build();
    	
    	ResponseSpecification responseSpec = new ResponseSpecBuilder()
    			.expectStatusCode(200)
    			.build();
    	
    	playerApiHelper = new PlayerApiHelper(requestSpec, responseSpec);
    	
	}
	
    @Test
    public void readPlayerBalance_whenSharedReadLock() throws NumberFormatException, JsonProcessingException, InterruptedException {

    	Player requestBody = Player.builder()
    			.name("Jun King")
    			.balance(new BigDecimal("10000"))
    			.build();

    	Integer playerId = playerApiHelper.create(requestBody);
    	
    	Assertions.assertNotNull(playerId, "Player ID should NOT be null!");
    	
    	// Multi
		ExecutorService executor = Executors.newCachedThreadPool();
		int numOfThreads = 3;

		IntStream.range(0, numOfThreads).forEach(count -> {
			executor.execute(() -> {
        		System.out.println("Multi read - #" + count);
        		try {
					playerApiHelper.get(playerId);
				} catch (NumberFormatException | JsonProcessingException e) {
					e.printStackTrace();
				}
			});
		});
		
		executor.shutdown();
		executor.awaitTermination(60, TimeUnit.SECONDS);	
    	
    }
	
    @Test
    public void bet_whenExclusiveLock() throws NumberFormatException, JsonProcessingException, InterruptedException {

    	Player requestBody = Player.builder()
    			.name("Jun King")
    			.balance(new BigDecimal("10000"))
    			.build();

    	Integer playerId = playerApiHelper.create(requestBody);
    	
    	Assertions.assertNotNull(playerId, "Player ID should NOT be null!");
    	
		ExecutorService executor = Executors.newCachedThreadPool();
		int numOfThreads = 2;

		IntStream.range(0, numOfThreads).forEach(count -> {
			executor.execute(() -> {
        		System.out.println("Multi write - #" + count);
        		try {
					playerApiHelper.bet(playerId, new BigDecimal("1000"));
				} catch (NumberFormatException | JsonProcessingException e) {
					e.printStackTrace();
				}
			});
		});
		
		executor.shutdown();
		executor.awaitTermination(60, TimeUnit.SECONDS);	

    	HashMap<String, Object> resp = playerApiHelper.get(playerId);
    	
    	Assertions.assertEquals(playerId, resp.get("id"));
    	Assertions.assertEquals(Float.valueOf("8000.0"), resp.get("balance"));
    	
    }
    
}
