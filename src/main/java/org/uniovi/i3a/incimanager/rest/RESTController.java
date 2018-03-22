/*
 * This source file is part of the rest-service open source project.
 *
 * Copyright (c) 2018 willy and the rest-service project authors.
 * Licensed under GNU General Public License v3.0.
 *
 * See /LICENSE for license information.
 * 
 */
package org.uniovi.i3a.incimanager.rest;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * Instance of RESTController.java
 * 
 * @author Guillermo Facundo Colunga
 * @version 201803152243
 */
@Slf4j
@EntityScan
@RestController
public class RESTController {
	
	@Autowired
	AgentsConnection agentsConnection;

	@RequestMapping(value = "/sensor-feed", method = RequestMethod.POST, consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> processSensorRequest( @RequestBody Map<String, Object> payload ) {
		
		// Authentication of the agent that made the request.
		val authenticationResponse = agentsConnection.executeQuery( new AgentsQueryFormatter( payload ).query() );
		
		if(authenticationResponse.getStatus() != HttpStatus.OK.value()) {
			log.warn( "[ERROR] UNAUTHORIZED ACCESS: trying to access as: "
						+ payload.get( "login" ) + " "
						+ payload.get( "password" ) + " "
						+ payload.get( "kind" ));
			
			return new ResponseEntity<String>("{\"response\":\"UNAUTHORIZED ACCESS WILL BE REPORTED\"}", HttpStatus.UNAUTHORIZED );
		}
		
		// Process the message in the request.
		@SuppressWarnings({ "unchecked" })
		val message = (LinkedHashMap<String, Object>) payload.get( "message" );
		message.put( "name", authenticationResponse.getBody().getObject().get( "name" ) );
		message.put( "location", authenticationResponse.getBody().getObject().get( "location" ) );
		message.put( "email", authenticationResponse.getBody().getObject().get( "email" ) );
		message.put( "id", authenticationResponse.getBody().getObject().get( "id" ) );
		message.put( "kind", authenticationResponse.getBody().getObject().get( "kind" ) );
		message.put( "kindCode", authenticationResponse.getBody().getObject().get( "kindCode" ) );
		
		System.out.println( message );
		
		// Send the message to Apache Kafka | Database
		// kafkaService.sendIncidence(message);
		
		// If all went OK return OK status.
		return new ResponseEntity<String>("{\"response\":\"request processed\"}", HttpStatus.OK );
	}
}
