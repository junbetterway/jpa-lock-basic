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
package org.junbetterway.multithreading.lockdemo.core;

import static io.restassured.RestAssured.given;
import static io.restassured.path.json.JsonPath.from;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

/**
 * 
 * @author JunMinon
 *
 */
@SuppressWarnings("unchecked")
public class IntegrationUtils {
    
    static {
        RestAssured.baseURI = "http://localhost";
    }

    public static <T> T performServerPost(final RequestSpecification requestSpec, 
    									  final ResponseSpecification responseSpec,
                                          final String postURL,
										  final String jsonBodyToSend,
                                          final String jsonAttributeToGetBack) {   	
        final String json = given().spec(requestSpec)
        		.body(jsonBodyToSend)
        		.expect().spec(responseSpec)
        		.log().ifError()
        		.when()
        		.post(postURL)
                .andReturn()
                .asString();
        
        if (null == jsonAttributeToGetBack) {
            return (T) json;
        }
        
        return (T) from(json).get(jsonAttributeToGetBack);
        
    }
    
    public static <T> T performServerGet(final RequestSpecification requestSpec,
										 final ResponseSpecification responseSpec,
										 final String getURL, 
										 final String jsonAttributeToGetBack) {
    	
        final String json = given().spec(requestSpec)
        		.expect().spec(responseSpec)
        		.log().ifError()
        		.when()
        		.get(getURL)
        		.andReturn()
        		.asString();
        
        if (null == jsonAttributeToGetBack) {
            return (T) json;
        }
        
        return (T) from(json).get(jsonAttributeToGetBack);
        
    }
    
    public static <T> T performServerPut(final RequestSpecification requestSpec, 
    									 final ResponseSpecification responseSpec,
                                         final String putURL, 
                                         final String jsonBodyToSend, 
                                         final String jsonAttributeToGetBack) {
        final String json = given().spec(requestSpec)
        		.body(jsonBodyToSend)
        		.expect().spec(responseSpec)
        		.log().ifError()
        		.when()
        		.put(putURL)
                .andReturn()
                .asString();
        
        if (null == jsonAttributeToGetBack) {
            return (T) json;
        }
        
        return (T) from(json).get(jsonAttributeToGetBack);
        
    }

}
