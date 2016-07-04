package com.intermancer.predictor.svc.endpoint;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;
import com.intermancer.predictor.experiment.ExperimentCycleResult;
import com.intermancer.predictor.experiment.ExperimentPrimeRunner;

@Path("/experimentCycle")
@Produces(MediaType.APPLICATION_JSON)
public class ExperimentCycleEP {
	
	private ExperimentPrimeRunner experimentRunner;
	
	public ExperimentCycleEP(ExperimentPrimeRunner experimentRunner) {
		this.experimentRunner = experimentRunner;
	}
	
	@GET
	@Timed
	public ExperimentCycleResult runSingleExperimentCycle() throws Exception {
		return experimentRunner.runExperimentCycle();
	}

}
