package com.intermancer.predictor.svc.endpoint;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;
import com.intermancer.predictor.experiment.ExperimentPrimeRunner;
import com.intermancer.predictor.svc.dom.StoreStatus;

@Path("/store")
@Produces(MediaType.APPLICATION_JSON)
public class OrganismStoreStatusEP {
	
	private ExperimentPrimeRunner experimentRunner;

	public OrganismStoreStatusEP(ExperimentPrimeRunner experimentRunner) {
		this.experimentRunner = experimentRunner;
	}
	
	@GET
	@Timed
	public StoreStatus storeStatus() {
		StoreStatus storeStatus = new StoreStatus(experimentRunner.getOrganismStore());
		return storeStatus;
	}

}
