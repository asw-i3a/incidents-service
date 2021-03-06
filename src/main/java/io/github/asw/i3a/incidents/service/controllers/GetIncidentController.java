package io.github.asw.i3a.incidents.service.controllers;

import java.util.List;

import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.asw.i3a.incidents.service.types.Incident;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class GetIncidentController extends AbstractIncidentController {

	@RequestMapping(value = "/incidents")
	public List<Incident> getIncidents( @Nullable @RequestParam("status") String status,
			@Nullable @RequestParam("operatorId") String operatorId,
			@Nullable @RequestParam("agentId") String agentId ) {

		List<Incident> result;

		if (agentId != null) {
			return service.findByAgentId( agentId );
		}

		if (status == null && operatorId == null) {
			result = service.findAll();
		} else if (status != null && operatorId == null) {
			result = service.findByStatus( status.toUpperCase() );
		} else if (status == null && operatorId != null) {
			result = service.findByOperatorId( operatorId );
		} else {
			result = service.findByOperatorId( operatorId );
			result.stream().filter( i -> i.getStatus().equalsIgnoreCase( status.toUpperCase() ) );
		}

		return result;
	}

	@RequestMapping(value = "/incidents/{id}")
	public Incident getIncident( @PathVariable("id") String id ) {
		log.info( "Geting incident from service with id: " + id );
		return service.findById( id );
	}
}
