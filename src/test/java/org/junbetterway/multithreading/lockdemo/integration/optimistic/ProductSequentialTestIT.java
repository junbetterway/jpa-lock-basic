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
package org.junbetterway.multithreading.lockdemo.integration.optimistic;

import java.util.HashMap;

import org.junbetterway.multithreading.lockdemo.optimistic.Product;
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
public class ProductSequentialTestIT {

	private static ProductApiHelper productApiHelper;
	
	@BeforeAll
	public static void setup() {
		
    	RequestSpecification requestSpec = new RequestSpecBuilder()
        		.setContentType(ContentType.JSON)
        		.build();
    	
    	ResponseSpecification responseSpec = new ResponseSpecBuilder()
    			.expectStatusCode(200)
    			.build();
    	
    	productApiHelper = new ProductApiHelper(requestSpec, responseSpec);
    	
	}
	
    @Test
    public void createProduct() throws NumberFormatException, JsonProcessingException {
    	
    	Product requestBody = Product.builder()
    			.name("Shirt")
    			.stocks(100)
    			.build();
    	
    	Integer productId = productApiHelper.createProduct(requestBody);
    	
    	Assertions.assertNotNull(productId, "Product ID should NOT be null!");
        
    }
    
    @Test
    public void updateName() throws NumberFormatException, JsonProcessingException {
    	
    	Product requestBody = Product.builder()
    			.name("Shoes")
    			.stocks(100)
    			.build();

    	Integer productId = productApiHelper.createProduct(requestBody);
    	
    	Assertions.assertNotNull(productId, "Product ID should NOT be null!");
    	
    	requestBody.setName("Sports Shoes");
    	
    	HashMap<String, Object> resp = productApiHelper.updateProduct(
    			productId, requestBody);
        
    	Assertions.assertEquals(productId, resp.get("id"));
    	Assertions.assertEquals("Sports Shoes", resp.get("name"));
    	
    }

    @Test
    public void createOrder() throws NumberFormatException, JsonProcessingException {
    	
    	Product requestBody = Product.builder()
    			.name("Bag")
    			.stocks(100)
    			.build();

    	Integer productId = productApiHelper.createProduct(requestBody);
    	
    	Assertions.assertNotNull(productId, "Product ID should NOT be null!");
    	
    	HashMap<String, Object> resp = productApiHelper.createOrder(productId, 5);
    	
    	Assertions.assertEquals(productId, resp.get("id"));
    	Assertions.assertEquals(95, resp.get("stocks"));
    	
    }
    
}
