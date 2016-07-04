package com.intermancer.predictor.svc.dom;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExperimentFinalStatus {
	
	private StoreStatus preStoreStatus;
	private StoreStatus postStoreStatus;
	private int replacementCount;

	@JsonProperty
	public StoreStatus getPreStoreStatus() {
		return preStoreStatus;
	}
	
	@JsonProperty
	public void setPreStoreStatus(StoreStatus preStoreStatus) {
		this.preStoreStatus = preStoreStatus;
	}
	
	@JsonProperty
	public StoreStatus getPostStoreStatus() {
		return postStoreStatus;
	}
	
	@JsonProperty
	public void setPostStoreStatus(StoreStatus postStoreStatus) {
		this.postStoreStatus = postStoreStatus;
	}
	
	@JsonProperty
	public int getReplacementCount() {
		return replacementCount;
	}
	
	@JsonProperty
	public void setReplacementCount(int replacementCount) {
		this.replacementCount = replacementCount;
	}
	
	@JsonProperty
	public double getLowestDeltaAbs() {
		return preStoreStatus.getLowScore() - postStoreStatus.getLowScore();
	}
	
	@JsonProperty
	public double getLowestDeltaPerc() {
		return getLowestDeltaAbs() / preStoreStatus.getLowScore();
	}
	
	@JsonProperty
	public double getHighestDeltaAbs() {
		return preStoreStatus.getHighScore() - postStoreStatus.getHighScore();
	}
	
	@JsonProperty
	public double getHighestDeltaPerc() {
		return getHighestDeltaAbs() / preStoreStatus.getHighScore();
	}

}
