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

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/**
 * 
 * @author JunMinon
 *
 */
@RequestMapping("api/player")
@RestController
@RequiredArgsConstructor
public class PlayerController {

	private final PlayerService service;
	
	@GetMapping("{playerId}")
	public ResponseEntity<Player> get(@PathVariable Long playerId) {
		
		return service.findByIdWithReadLock(playerId)
				.map(player -> new ResponseEntity<>(player, HttpStatus.OK))
				.orElseThrow(() -> new RuntimeException("Cannot get this player info!"));
	}
	
	@PostMapping
	public ResponseEntity<Long> create(@RequestBody Player newPlayer) {
		
		return service.save(newPlayer)
				.map(product -> new ResponseEntity<>(product.getId(), HttpStatus.OK))
				.orElseThrow(() -> new RuntimeException("Cannot create new player!"));
	}

	@PostMapping("{playerId}/bet")
	public ResponseEntity<Player> bet(@PathVariable Long playerId, @RequestBody Player playerRequest) {
		
		return service.betWithWriteLock(playerId, playerRequest.getBetAmount())
				.map(player -> new ResponseEntity<>(player, HttpStatus.OK))
				.orElseThrow(() -> new RuntimeException("Cannot bet on this player!"));
	}
	
}
