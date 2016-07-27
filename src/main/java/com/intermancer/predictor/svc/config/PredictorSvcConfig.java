package com.intermancer.predictor.svc.config;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

public class PredictorSvcConfig extends Configuration {
	
	@NotNull
	private Integer maxStoreSize;
	
	@NotEmpty
	private String organismStorePath;
	
	@NotNull
	private Integer experimentCycles;

	@JsonProperty
	public Integer getMaxStoreSize() {
		return maxStoreSize;
	}

	@JsonProperty
	public void setMaxStoreSize(Integer maxStoreSize) {
		this.maxStoreSize = maxStoreSize;
	}

	@JsonProperty
	public String getOrganismStorePath() {
		return organismStorePath;
	}

	@JsonProperty
	public void setOrganismStorePath(String organismStorePath) {
		this.organismStorePath = organismStorePath;
	}

	@JsonProperty
	public Integer getExperimentCycles() {
		return experimentCycles;
	}

	@JsonProperty
	public void setExperimentCycles(Integer experimentCycles) {
		this.experimentCycles = experimentCycles;
	}

}
