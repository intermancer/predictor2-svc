package com.intermancer.predictor.svc.dom;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intermancer.predictor.organism.store.OrganismStore;
import com.intermancer.predictor.organism.store.OrganismStoreRecord;

public class StoreStatus {
	
	private long size;
	private long maxSize;
	
	private double highScore;
	private double lowScore;
	
	private List<OrganismStoreRecord> top5Records;
	
	public StoreStatus() {
		// For Jackson
	}
	
	public StoreStatus(OrganismStore organismStore) {
		setSize(organismStore.getCount());
		setMaxSize(organismStore.getMaxSize());
		List<OrganismStoreRecord> top5Records = new ArrayList<OrganismStoreRecord>();
		for (int i = 0; i < 5; i++) {
			top5Records.add(organismStore.findByIndex(i));
		}
		setTop5Records(top5Records);
		setHighScore(organismStore.getHighestScore());
		setLowScore(organismStore.getLowestScore());
	}

	public StoreStatus(int size) {
		this.size = size;
	}

	@JsonProperty
	public long getSize() {
		return size;
	}

	@JsonProperty
	public void setSize(long size) {
		this.size = size;
	}

	@JsonProperty
	public long getMaxSize() {
		return maxSize;
	}

	@JsonProperty
	public void setMaxSize(long maxSize) {
		this.maxSize = maxSize;
	}

	@JsonProperty
	public List<OrganismStoreRecord> getTop5Records() {
		return top5Records;
	}

	@JsonProperty
	public void setTop5Records(List<OrganismStoreRecord> top5Records) {
		this.top5Records = top5Records;
	}

	@JsonProperty
	public double getHighScore() {
		return highScore;
	}

	@JsonProperty
	public void setHighScore(double highScore) {
		this.highScore = highScore;
	}

	@JsonProperty
	public double getLowScore() {
		return lowScore;
	}

	@JsonProperty
	public void setLowScore(double lowScore) {
		this.lowScore = lowScore;
	}	

}
