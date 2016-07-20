package com.intermancer.predictor.svc.endpoint;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.codahale.metrics.annotation.Timed;
import com.intermancer.predictor.experiment.AnalysisExperimentListener;
import com.intermancer.predictor.experiment.ExperimentContext;
import com.intermancer.predictor.experiment.ExperimentPrimeRunner;
import com.intermancer.predictor.experiment.ExperimentResult;
import com.intermancer.predictor.svc.dom.ExperimentStatus;

@Path("/experiment")
@Produces(MediaType.APPLICATION_JSON)
public class ExperimentRunEP {
	
	private static final Logger logger = LogManager.getLogger(ExperimentRunEP.class);
		
	private ExperimentPrimeRunner experimentRunner;
	private Thread backgroundThreadForRunner;
	private long beginningExecutionTime;
	private AnalysisExperimentListener analysisListener;
	
	public ExperimentRunEP(ExperimentPrimeRunner experimentRunner) {
		this.experimentRunner = experimentRunner;
		analysisListener = new AnalysisExperimentListener(experimentRunner.getOrganismStore());
		experimentRunner.addExperimentListener(analysisListener);
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
		ExperimentContext context = experimentRunner.getContext();
		experimentStatus.setCycles(context.getCycles());
		experimentStatus.setIteration(context.getIteration());
		experimentStatus.setContinueExperimenting(experimentRunner.isContinueExperimenting());
		experimentStatus.setThreadAlreadyExecuting(backgroundThreadForRunner != null);
		experimentStatus.setExecutionTime(System.currentTimeMillis() - beginningExecutionTime);
		return experimentStatus;
	}
	
	@Path("/setCycles/{cycles}")
	@GET
	public ExperimentStatus setCycles(@PathParam("cycles") Integer cycles) {
		experimentRunner.getContext().setCycles(cycles);
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
		experimentRunner.getContext().setCycles(0);
		return experimentStatus;
	}
	
	@Path("/lastExperimentResult")
	@GET
	public ExperimentResult getLastExperimentResult() {
		logger.info("Getting experiment result...");
		ExperimentResult result = analysisListener.getExperimentResult();
		logger.info(result.toString());
		return result;
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
