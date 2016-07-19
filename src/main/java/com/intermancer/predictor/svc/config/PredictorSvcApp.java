package com.intermancer.predictor.svc.config;

import com.intermancer.predictor.experiment.ExperimentPrimeRunner;
import com.intermancer.predictor.svc.endpoint.OrganismStoreStatusEP;
import com.intermancer.predictor.svc.endpoint.ExperimentRunEP;
import com.intermancer.predictor.svc.health.ExperimentPrimeRunnerHealthCheck;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class PredictorSvcApp extends Application<PredictorSvcConfig> {

	private ExperimentPrimeRunner experimentRunner;

	public static void main(String[] args) throws Exception {
		new PredictorSvcApp().run(args);
	}

	@Override
	public String getName() {
		return "predictor-svc";
	}

	@Override
	public void run(PredictorSvcConfig config, Environment env) throws Exception {
		setUpExperiment(config);
		setUpEndpoints(config, env);
		setUpHealthChecks(env);
	}

	private void setUpHealthChecks(Environment env) {
		final ExperimentPrimeRunnerHealthCheck storeHC = new ExperimentPrimeRunnerHealthCheck(experimentRunner);
		env.healthChecks().register("store", storeHC);
	}

	private void setUpEndpoints(PredictorSvcConfig config, Environment env) throws Exception {
		final OrganismStoreStatusEP organismStoreStatusEP = new OrganismStoreStatusEP(experimentRunner, config);
		env.jersey().register(organismStoreStatusEP);
		final ExperimentRunEP experimentRunEP = new ExperimentRunEP(experimentRunner);
		env.jersey().register(experimentRunEP);
	}

	private void setUpExperiment(PredictorSvcConfig config) throws Exception {
		experimentRunner = new ExperimentPrimeRunner();
		experimentRunner.setDiskStorePath(config.getOrganismStorePath());
		experimentRunner.init();
	}

}
