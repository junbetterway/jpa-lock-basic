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
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * 
 * @author JunMinon
 *
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class ProductService {

    private final ProductRepository repository;
    
    @Transactional
    public Optional<Product> save(final Product newProd) {
        return Optional.ofNullable(repository.save(newProd));
    }

    @Transactional
    public Optional<Product> findById(final Long id) {
        return repository.findById(id);
    }
    
    @Transactional
    public Optional<Product> updateName(final Long id, final String name) {
        
        Optional<Product> product = repository.findById(id);
        
        if(!product.isPresent()) {
        	return Optional.empty();
        } 

        Product existingProduct = product.get();
        existingProduct.setName(name);
    	
        return Optional.ofNullable(repository.save(existingProduct));
        
    }
    
    @Transactional
    public Optional<Product> order(final Long id, final int orderCount) {

        Optional<Product> product = repository.findById(id);

        if(!product.isPresent()) {
        	return Optional.empty();
        } 

        Product existingProduct = product.get();
        
        if(existingProduct.getStocks() < orderCount) {
        	throw new RuntimeException("Not enough stocks!");
        }
        
        existingProduct.setStocks(existingProduct.getStocks() - orderCount);

    	existingProduct = repository.saveAndFlush(existingProduct);
        
        return Optional.ofNullable(existingProduct);
        
    }  
    
    @Transactional
    public Optional<Product> orderCatchingException(final Long id, final int orderCount) {

        Optional<Product> product = repository.findById(id);

        if(!product.isPresent()) {
        	return Optional.empty();
        } 

        Product existingProduct = product.get();
        
        if(existingProduct.getStocks() < orderCount) {
        	throw new RuntimeException("Not enough stocks!");
        }
        
        existingProduct.setStocks(existingProduct.getStocks() - orderCount);
    	
        try {
        	existingProduct = repository.saveAndFlush(existingProduct);
		} catch (Exception e) {
			log.error("Ordering caught an exception? {}", e.getMessage());
			throw new NetworkBusyException();
		}
        
        return Optional.ofNullable(existingProduct);
        
    }    

	@Retryable(value = { StaleStateException.class, ConcurrencyFailureException.class }, 
			   maxAttempts = 2, 
			   backoff = @Backoff(delay = 1000))
    @Transactional
    public Optional<Product> orderWithRetry(final Long id, final int orderCount) {

		final int retry = RetrySynchronizationManager.getContext().getRetryCount();

		log.info("[ORDER] Attempt#{} for product ID:  {}", retry + 1, id);
		
        Optional<Product> product = repository.findById(id);

        if(!product.isPresent()) {
        	return Optional.empty();
        } 

        Product existingProduct = product.get();
        
        if(existingProduct.getStocks() < orderCount) {
        	throw new RuntimeException("Not enough stocks!");
        }
        
        existingProduct.setStocks(existingProduct.getStocks() - orderCount);

    	existingProduct = repository.save(existingProduct);
        
        return Optional.ofNullable(existingProduct);
        
    }
    
}
