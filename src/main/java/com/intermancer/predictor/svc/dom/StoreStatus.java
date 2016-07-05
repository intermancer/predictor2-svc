package com.intermancer.predictor.svc.dom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intermancer.predictor.gene.Chromosome;
import com.intermancer.predictor.gene.Gene;
import com.intermancer.predictor.organism.Organism;
import com.intermancer.predictor.organism.store.OrganismStore;
import com.intermancer.predictor.organism.store.OrganismStoreRecord;

public class StoreStatus {
	
	private long size;
	private long maxSize;
	
	private double highScore;
	private double lowScore;
	
	private List<OrganismStoreRecord> top5Records;
	private Map<Class, Long> histogram;
	
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
		createHistogram(organismStore);
		setHighScore(organismStore.getHighestScore());
		setLowScore(organismStore.getLowestScore());
	}

	private void createHistogram(OrganismStore organismStore) {
		histogram = new HashMap<Class, Long>();
		for (int i = 0; i < organismStore.getCount(); i++) {
			Organism organism = organismStore.findByIndex(i).getOrganism();
			for (Chromosome chromosome : organism.getChromosomes()) {
				for (Gene gene : chromosome.getGenes()) {
					Long histogramCount = histogram.get(gene.getClass());
					if (histogramCount == null) {
						histogram.put(gene.getClass(), new Long(1));
					} else {
						histogram.put(gene.getClass(), new Long(histogramCount.longValue() + 1));
					}
				}
			}
		}
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

	@JsonProperty
	public Map<Class, Long> getHistogram() {
		return histogram;
	}

	@JsonProperty
	public void setHistogram(Map<Class, Long> histogram) {
		this.histogram = histogram;
	}
	
}
