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
package org.junbetterway.multithreading.lockdemo.pessimistic;

import java.math.BigDecimal;
import java.util.Optional;

import org.junbetterway.multithreading.lockdemo.core.NetworkBusyException;
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
public class PlayerService {

    private final PlayerRepository repository;
    
    @Transactional
    public Optional<Player> save(final Player player) {
        return Optional.ofNullable(repository.save(player));
    }

    @Transactional
    public Optional<Player> findById(final Long id) {
        return repository.findById(id);
    }

    @Transactional
    public Optional<Player> findByIdWithReadLock(final Long id) {
        return repository.findWithReadLockById(id);
    }
    
    @Transactional
    public Optional<Player> bet(final Long id, final BigDecimal betAmount) {

        Optional<Player> player = repository.findById(id);

        if(!player.isPresent()) {
        	return Optional.empty();
        } 

        Player existingPlayer = player.get();
        
        if(existingPlayer.getBalance().compareTo(betAmount) < 0) {
        	throw new RuntimeException("Not enough balance!");
        }
        
        existingPlayer.setBalance(existingPlayer.getBalance().subtract(betAmount));
    	
        try {
        	existingPlayer = repository.saveAndFlush(existingPlayer);
		} catch (Exception e) {
			log.error("Betting caught an exception? {}", e.getMessage());
			throw new NetworkBusyException();
		}
        
        return Optional.ofNullable(existingPlayer);
        
    }
    
    @Transactional
    public Optional<Player> betWithWriteLock(final Long id, final BigDecimal betAmount) {

        Optional<Player> player = repository.findByIdWithWriteLock(id);

        if(!player.isPresent()) {
        	return Optional.empty();
        } 

        Player existingPlayer = player.get();
        
        if(existingPlayer.getBalance().compareTo(betAmount) < 0) {
        	throw new RuntimeException("Not enough balance!");
        }
        
        existingPlayer.setBalance(existingPlayer.getBalance().subtract(betAmount));
    	
        try {
        	existingPlayer = repository.saveAndFlush(existingPlayer);
		} catch (Exception e) {
			log.error("Betting caught an exception? {}", e.getMessage());
			throw new NetworkBusyException();
		}
        
        return Optional.ofNullable(existingPlayer);
        
    }
    
}
