package com.intermancer.predictor.feeder;

import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;

import com.intermancer.predictor.data.ConsumeResponse;
import com.intermancer.predictor.data.Quantum;
import com.intermancer.predictor.evaluator.Evaluator;

public class FeedAnalyzer implements FeedCycleListener {
	
	private static final int DEFAULT_DATA_SET_SIZE = 100;

	private TimeSeries organismData;
	private TimeSeries trainingData;

	private Evaluator evaluator;

	@Override
	public void init(Feeder feeder) {
		organismData = new TimeSeries("Organism data");
		organismData.setMaximumItemCount(DEFAULT_DATA_SET_SIZE);
		trainingData = new TimeSeries("Training data");
		trainingData.setMaximumItemCount(DEFAULT_DATA_SET_SIZE);
		evaluator = feeder.getEvaluator();
	}

	@Override
	public boolean handle(ConsumeResponse consumeResponse, Quantum quantum) {
		long currentTime = System.currentTimeMillis();
		organismData.addOrUpdate(new FixedMillisecond(currentTime),
				quantum.getChannel(evaluator.getTargetOffset()).getValue().doubleValue());
		trainingData.addOrUpdate(new FixedMillisecond(currentTime),
				quantum.getChannel(evaluator.getEvaluationOffset()).getValue().doubleValue());
		return true;
	}
	
	public TimeSeries getOrganismData() {
		return organismData;
	}
	
	public TimeSeries getTrainingData() {
		return trainingData;
	}

}
