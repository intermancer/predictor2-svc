package com.intermancer.predictor.svc.endpoint;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;
import com.intermancer.predictor.experiment.ExperimentPrimeRunner;
import com.intermancer.predictor.organism.store.OrganismStoreRecord;
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
	public StoreStatus defaultRequest() {
		return storeStatus();
	}
	
	@Path("/status")
	@GET
	@Timed
	public StoreStatus storeStatus() {
		StoreStatus storeStatus = new StoreStatus(experimentRunner.getOrganismStore());
		return storeStatus;
	}
	
	@Path("/dumpTopOrganismStoreRecords/{numberToDump}")
	@GET
	@Timed
	public List<OrganismStoreRecord> dumpTop(@PathParam("numberToDump") int numberToDump) {
		List<OrganismStoreRecord> topOrganisms = new ArrayList<OrganismStoreRecord>();
		for (int i = 0; i < numberToDump; i++) {
			OrganismStoreRecord record = experimentRunner.getOrganismStore().findByIndex(i); 
			topOrganisms.add(record);
			
		}
		return topOrganisms;
	}

}
