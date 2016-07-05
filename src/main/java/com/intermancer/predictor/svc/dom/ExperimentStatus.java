package com.intermancer.predictor.svc.dom;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExperimentStatus {
	
	private int cycles;
	private int iteration;
	private boolean continueExperimenting;
	private boolean threadAlreadyExecuting;
	private long executionTime;
	
	@JsonProperty
	public int getCycles() {
		return cycles;
	}
	
	@JsonProperty
	public void setCycles(int cycles) {
		this.cycles = cycles;
	}
	
	@JsonProperty
	public int getIteration() {
		return iteration;
	}
	
	@JsonProperty
	public void setIteration(int iteration) {
		this.iteration = iteration;
	}
	
	@JsonProperty
	public boolean isContinueExperimenting() {
		return continueExperimenting;
	}
	
	@JsonProperty
	public void setContinueExperimenting(boolean continueExperimenting) {
		this.continueExperimenting = continueExperimenting;
	}

	@JsonProperty
	public boolean isThreadAlreadyExecuting() {
		return threadAlreadyExecuting;
	}

	@JsonProperty
	public void setThreadAlreadyExecuting(boolean threadAlreadyExecuting) {
		this.threadAlreadyExecuting = threadAlreadyExecuting;
	}

	@JsonProperty
	public long executionTime() {
		return executionTime;
	}

	@JsonProperty
	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}	

}
