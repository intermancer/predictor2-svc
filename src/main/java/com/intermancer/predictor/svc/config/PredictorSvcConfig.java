package com.intermancer.predictor.svc.config;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

public class PredictorSvcConfig extends Configuration {
	
	@NotEmpty
	private String maxStoreSize;
	
	@NotEmpty
	private String organismStorePath;

	@JsonProperty
	public String getMaxStoreSize() {
		return maxStoreSize;
	}

	@JsonProperty
	public void setMaxStoreSize(String maxStoreSize) {
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

}
