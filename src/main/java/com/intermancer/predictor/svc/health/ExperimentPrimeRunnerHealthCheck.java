package com.intermancer.predictor.svc.health;

import com.codahale.metrics.health.HealthCheck;
import com.intermancer.predictor.experiment.ExperimentPrimeRunner;

public class ExperimentPrimeRunnerHealthCheck extends HealthCheck{
	
	private ExperimentPrimeRunner experimentRunner;
	
	public ExperimentPrimeRunnerHealthCheck(ExperimentPrimeRunner experimentRunner) {
		this.experimentRunner = experimentRunner;
	}

	@Override
	protected Result check() throws Exception {
		if (experimentRunner == null) {
			return Result.unhealthy("experimentPrime is null.");
		}
		return Result.healthy();
	}

}
