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
package org.junbetterway.multithreading.lockdemo.optimistic;

import java.util.Optional;

import org.hibernate.StaleStateException;
import org.junbetterway.multithreading.lockdemo.core.NetworkBusyException;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * 
 * @author JunMinon
 *
 */
@RequestMapping("api/product")
@RestController
@RequiredArgsConstructor
@Log4j2
public class ProductController {

	private final ProductService productService;
	
	@GetMapping("{productId}")
	public ResponseEntity<Product> get(@PathVariable Long productId) {
		
		return productService.findById(productId)
				.map(product -> new ResponseEntity<>(product, HttpStatus.OK))
				.orElseThrow(() -> new RuntimeException("Cannot get this product!"));
	}
	
	@PostMapping
	public ResponseEntity<Long> create(@RequestBody Product newProduct) {
		
		return productService.save(newProduct)
				.map(product -> new ResponseEntity<>(product.getId(), HttpStatus.OK))
				.orElseThrow(() -> new RuntimeException("Cannot create product!"));
	}

//	@PostMapping("order/{productId}/count/{orderCount}")
//	public ResponseEntity<Product> order(@PathVariable Long productId, @PathVariable int orderCount) {
//		
//		log.info("Performing order for product ID: {}", productId);
//		
//		return productService.orderWithRetry(productId, orderCount)
//				.map(product -> new ResponseEntity<>(product, HttpStatus.OK))
//				.orElseThrow(() -> new RuntimeException("Cannot order this product!"));
//	}
	
	@PutMapping("update/name/{productId}")
	public ResponseEntity<Product> updateName(@PathVariable Long productId, 
											  @RequestBody Product newProduct) {
		
		return productService.updateName(productId, newProduct.getName())
				.map(product -> new ResponseEntity<>(product, HttpStatus.OK))
				.orElseThrow(() -> new RuntimeException("Cannot update name of product!"));
		
	}
	
	@PostMapping("order/{productId}/count/{orderCount}")
	public ResponseEntity<Product> orderWithMaxException(@PathVariable Long productId, @PathVariable int orderCount) {
		
		Optional<Product> product = Optional.empty();
		
		try {
			
			product = productService.orderWithRetry(productId, orderCount);
			
		} catch (StaleStateException | ConcurrencyFailureException e) {
			
			log.error("[ORDER] Concurrency failure max attempt reached for product ID: {}", productId);
			throw new NetworkBusyException();
			
		} catch (Exception e) {

			log.error("[Bet Creation] Generic failure for product ID: {} due to {} ", productId, e.getMessage());
			
		} 
	
		return product.map(prod -> new ResponseEntity<>(prod, HttpStatus.OK))
					  .orElseThrow(() -> new RuntimeException("Cannot order this product!"));
		
	}
	
}
