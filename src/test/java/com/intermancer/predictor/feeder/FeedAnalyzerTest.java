package com.intermancer.predictor.feeder;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.jfree.data.time.TimeSeries;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intermancer.predictor.experiment.ExperimentContext;
import com.intermancer.predictor.experiment.ExperimentPrimeRunner;
import com.intermancer.predictor.organism.BaseOrganism;
import com.intermancer.predictor.organism.Organism;

public class FeedAnalyzerTest {

	private static final String TEST_ORGANISM_PATH = "src/test/resources/com/intermancer/predictor/test/data/organisms/SimpleTestOrganismA.json";
	private static final ObjectMapper objectMapper = new ObjectMapper();

	private static ExperimentPrimeRunner experimentRunner;
	private static ExperimentContext context;
	private static FeedAnalyzer feedAnalyzer;
	private static Feeder feeder;

	@BeforeClass
	public static void init() {
		System.out.println("Starting init()...");
		experimentRunner = new ExperimentPrimeRunner();
		context = experimentRunner.getContext();
		feedAnalyzer = new FeedAnalyzer();
		feeder = context.getFeeder();
		List<FeedCycleListener> previousListeners = feeder.getFeedCycleListeners();
		List<FeedCycleListener> listenersWithFeedAnalyzer = new ArrayList<FeedCycleListener>();
		listenersWithFeedAnalyzer.addAll(previousListeners);
		listenersWithFeedAnalyzer.add(feedAnalyzer);
		feeder.setFeedCycleListeners(listenersWithFeedAnalyzer);
		System.out.println("Ending init()...");
	}

	@Test
	public void testFeedAnalyzer() throws Exception {

		feeder.setOrganism(getOrganism());
		feeder.init();
		feeder.feedOrganism();

		TimeSeries trainingData = feedAnalyzer.getTrainingData();
		assertEquals(trainingData.getItemCount(), FeedAnalyzer.DEFAULT_DATA_SET_SIZE);
		TimeSeries predictedData = feedAnalyzer.getPredictedData();
		assertEquals(predictedData.getItemCount(), FeedAnalyzer.DEFAULT_DATA_SET_SIZE);

		for (int i = 0; i < predictedData.getItemCount(); i++) {
			assertEquals(predictedData.getDataItem(i).getPeriod(), trainingData.getDataItem(i).getPeriod());
			assertTrue(predictedData.getDataItem(i).getValue().doubleValue() != 0.0);
			assertTrue(trainingData.getDataItem(i).getValue().doubleValue() != 0.0);
		}

	}

	private Organism getOrganism() throws Exception {
		Organism readInOrganism = null;
		Charset charset = Charset.forName("US-ASCII");
		Path testOrganismPath = Paths.get(TEST_ORGANISM_PATH);
		try (BufferedReader reader = Files.newBufferedReader(testOrganismPath, charset)) {
			readInOrganism = objectMapper.readValue(reader, BaseOrganism.class);
		}

		return readInOrganism;
	}

}
