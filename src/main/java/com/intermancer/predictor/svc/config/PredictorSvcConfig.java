package com.intermancer.predictor.svc.config;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

public class PredictorSvcConfig extends Configuration {
	
	@NotEmpty
	private String maxStoreSize;

	@JsonProperty
	public String getMaxStoreSize() {
		return maxStoreSize;
	}

	@JsonProperty
	public void setMaxStoreSize(String maxStoreSize) {
		this.maxStoreSize = maxStoreSize;
	}

}
