package com.intermancer.predictor.feeder;

import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;

import com.intermancer.predictor.data.ConsumeResponse;
import com.intermancer.predictor.data.Quantum;
import com.intermancer.predictor.evaluator.Evaluator;

public class FeedAnalyzer implements FeedCycleListener {

	public static final int DEFAULT_DATA_SET_SIZE = 100;

	private TimeSeries predictedData;
	private TimeSeries trainingData;

	private Evaluator evaluator;
	private int completeFeedCycleCount;

	@Override
	public void init(Feeder feeder) {
		predictedData = new TimeSeries("Predicted data");
		predictedData.setMaximumItemCount(DEFAULT_DATA_SET_SIZE);
		trainingData = new TimeSeries("Training data");
		trainingData.setMaximumItemCount(DEFAULT_DATA_SET_SIZE);
		evaluator = feeder.getEvaluator();
		completeFeedCycleCount++;
	}

	@Override
	public boolean handle(ConsumeResponse consumeResponse, Quantum quantum) {
		if (consumeResponse.equals(ConsumeResponse.CONSUME_COMPLETE)) {
			trainingData.addOrUpdate(new FixedMillisecond(quantum.getTimestamp()), evaluator.getTrainingValue());
			predictedData.addOrUpdate(new FixedMillisecond(quantum.getTimestamp()), evaluator.getPredictedValue());
			completeFeedCycleCount++;
		}
		return true;
	}

	public TimeSeries getPredictedData() {
		return predictedData;
	}

	public TimeSeries getTrainingData() {
		return trainingData;
	}

	public int getCompleteFeedCycleCount() {
		return completeFeedCycleCount;
	}

}
