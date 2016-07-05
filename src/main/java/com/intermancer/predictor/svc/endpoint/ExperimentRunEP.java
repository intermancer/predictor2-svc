package com.intermancer.predictor.svc.endpoint;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;
import com.intermancer.predictor.experiment.ExperimentPrimeRunner;
import com.intermancer.predictor.experiment.ExperimentResult;
import com.intermancer.predictor.svc.dom.ExperimentStatus;

@Path("/experiment")
@Produces(MediaType.APPLICATION_JSON)
public class ExperimentRunEP {
		
	private ExperimentPrimeRunner experimentRunner;
	private Thread backgroundThreadForRunner;
	private long beginningExecutionTime;
	
	public ExperimentRunEP(ExperimentPrimeRunner experimentRunner) {
		this.experimentRunner = experimentRunner;
	}
	
	@GET
	@Timed
	public ExperimentStatus defaultAction() {
		return getExperimentStatus();
	}
	
	@Path("/start")
	@GET
	@Timed
	public ExperimentStatus start() {
		ExperimentStatus experimentStatus = getExperimentStatus();
		experimentStatus.setThreadAlreadyExecuting(!startBackgroundThreadSafely());
		return experimentStatus;
	}
	
	@Path("/status")
	@GET
	@Timed
	public ExperimentStatus getExperimentStatus() {
		ExperimentStatus experimentStatus = new ExperimentStatus();
		experimentStatus.setCycles(experimentRunner.getCycles());
		experimentStatus.setIteration(experimentRunner.getIteration());
		experimentStatus.setContinueExperimenting(experimentRunner.isContinueExperimenting());
		experimentStatus.setThreadAlreadyExecuting(backgroundThreadForRunner != null);
		experimentStatus.setExecutionTime(System.currentTimeMillis() - beginningExecutionTime);
		return experimentStatus;
	}
	
	@Path("/setCycles/{cycles}")
	@GET
	public ExperimentStatus setCycles(@PathParam("cycles") Integer cycles) {
		experimentRunner.setCycles(cycles);
		return getExperimentStatus();
	}
	
	@Path("/setContinueExperimenting/{continueExperimenting}")
	@GET
	public Boolean setContinueExperimenting(@PathParam("continueExperimenting") boolean continueExperimenting) {
		experimentRunner.setContinueExperimenting(continueExperimenting);
		return new Boolean (experimentRunner.isContinueExperimenting());
	}
	
	@Path("/stop")
	@GET
	public ExperimentStatus stop() {
		ExperimentStatus experimentStatus = getExperimentStatus();
		experimentRunner.setContinueExperimenting(false);
		experimentRunner.setCycles(0);
		return experimentStatus;
	}
	
	@Path("/lastExperimentResult")
	@GET
	public ExperimentResult getLastExperimentResult() {
		return experimentRunner.getLastExperimentResult();
	}

	private synchronized boolean startBackgroundThreadSafely() {
		if ((backgroundThreadForRunner != null) && (backgroundThreadForRunner.isAlive())) {
			return false;
		} else {
			beginningExecutionTime = System.currentTimeMillis();
			backgroundThreadForRunner = new Thread(experimentRunner);
			backgroundThreadForRunner.start();
			return true;
		}
	}
	
}
