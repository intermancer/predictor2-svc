package com.intermancer.predictor.svc.endpoint;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.encoders.SunPNGEncoderAdapter;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intermancer.predictor.experiment.ExperimentContext;
import com.intermancer.predictor.experiment.ExperimentPrimeRunner;
import com.intermancer.predictor.feeder.FeedAnalyzer;
import com.intermancer.predictor.feeder.FeedCycleListener;
import com.intermancer.predictor.feeder.Feeder;
import com.intermancer.predictor.organism.Organism;
import com.intermancer.predictor.organism.store.OrganismStoreRecord;
import com.intermancer.predictor.svc.config.PredictorSvcConfig;
import com.intermancer.predictor.svc.dom.StoreStatus;

@Path("/store")
@Produces(MediaType.APPLICATION_JSON)
public class OrganismStoreStatusEP {

	private static final Charset charset = Charset.forName("US-ASCII");

	private static final int DEFAULT_CHART_HEIGHT = 600;
	private static final int DEFAULT_CHART_WIDTH = 800;

	private ExperimentPrimeRunner experimentRunner;
	private String organismStorePath;
	private static final ObjectMapper objectMapper = new ObjectMapper();

	public OrganismStoreStatusEP(ExperimentPrimeRunner experimentRunner, PredictorSvcConfig config) throws Exception {
		this.experimentRunner = experimentRunner;
		this.organismStorePath = config.getOrganismStorePath();
		java.nio.file.Path directoryPath = Paths.get(organismStorePath);
		Files.createDirectories(directoryPath);
	}

	@GET
	@Timed
	public StoreStatus defaultRequest() {
		return storeStatus();
	}

	@Path("/status")
	@GET
	@Timed
	public StoreStatus storeStatus() {
		StoreStatus storeStatus = new StoreStatus(experimentRunner.getContext().getOrganismStore());
		return storeStatus;
	}

	@Path("/dumpTop/{numberToDump}")
	@GET
	@Timed
	public List<OrganismStoreRecord> dumpTop(@PathParam("numberToDump") int numberToDump) throws Exception {
		List<OrganismStoreRecord> topOrganisms = new ArrayList<OrganismStoreRecord>();
		String dumptime = Long.toString(System.currentTimeMillis());
		for (int i = 0; i < numberToDump; i++) {
			OrganismStoreRecord record = experimentRunner.getContext().getOrganismStore().findByIndex(i);
			topOrganisms.add(record);
			String filename = dumptime + "_" + record.getIndex() + ".json";
			java.nio.file.Path filePath = Paths.get(organismStorePath, filename);
			try (BufferedWriter writer = Files.newBufferedWriter(filePath, charset)) {
				String jsonRepresentation = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(record);
				writer.write(jsonRepresentation);
			}
		}
		return topOrganisms;
	}
	
	@Path("/feedGraph/{index}")
	@GET
	@Timed
	@Produces("image/png")
	public Response getFeedGraph(@PathParam("index") int index) throws IOException {
		ExperimentContext context = experimentRunner.getContext();
		Organism organism = context.getOrganismStore().findByIndex(index).getOrganism();
		FeedAnalyzer feedAnalyzer = new FeedAnalyzer();
		Feeder feeder = context.getFeeder();
		List<FeedCycleListener> previousListeners = feeder.getFeedCycleListeners();
		List<FeedCycleListener> listenersWithFeedAnalyzer = new ArrayList<FeedCycleListener>();
		listenersWithFeedAnalyzer.addAll(previousListeners);
		listenersWithFeedAnalyzer.add(feedAnalyzer);
		feeder.setFeedCycleListeners(listenersWithFeedAnalyzer);

		feeder.setOrganism(organism);
		feeder.init();
		feeder.feedOrganism();
		
		// put listeners back the way they were.
		feeder.setFeedCycleListeners(previousListeners);
		return generateGraph(feedAnalyzer);
	}

	private Response generateGraph(FeedAnalyzer feedAnalyzer) throws IOException {
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		TimeSeries organismData = feedAnalyzer.getOrganismData();
		dataset.addSeries(organismData);
		TimeSeries trainingData = feedAnalyzer.getTrainingData();
		dataset.addSeries(trainingData);
		JFreeChart timechart = ChartFactory.createTimeSeriesChart("Organism Feed Chart", // Title
				"millisecond", // X-axis Label
				"value", // Y-axis Label
				dataset, // Dataset
				true, // Show legend
				true, // Use tooltips
				false // Generate URLs
		);
		SunPNGEncoderAdapter pngAdapter = new SunPNGEncoderAdapter();
		byte[] imageData = pngAdapter.encode(timechart.createBufferedImage(DEFAULT_CHART_WIDTH, DEFAULT_CHART_HEIGHT));
		return Response.ok(imageData).build();
	}

}
