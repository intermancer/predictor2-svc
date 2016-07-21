package com.intermancer.predictor.svc.endpoint;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.encoders.SunPNGEncoderAdapter;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import com.codahale.metrics.annotation.Timed;
import com.intermancer.predictor.experiment.ExperimentContext;
import com.intermancer.predictor.experiment.ExperimentPrimeRunner;
import com.intermancer.predictor.experiment.ExperimentResult;
import com.intermancer.predictor.experiment.analysis.AnalysisExperimentListener;
import com.intermancer.predictor.svc.dom.ExperimentStatus;

@Path("/experiment")
@Produces(MediaType.APPLICATION_JSON)
public class ExperimentRunEP {

	private static final Logger logger = LogManager.getLogger(ExperimentRunEP.class);

	private static final int DEFAULT_CHART_HEIGHT = 600;
	private static final int DEFAULT_CHART_WIDTH = 800;

	private ExperimentPrimeRunner experimentRunner;
	private Thread backgroundThreadForRunner;
	private long beginningExecutionTime;
	private AnalysisExperimentListener analysisListener;

	public ExperimentRunEP(ExperimentPrimeRunner experimentRunner) {
		this.experimentRunner = experimentRunner;
		analysisListener = new AnalysisExperimentListener(experimentRunner.getContext().getOrganismStore());
		experimentRunner.addExperimentListener(analysisListener);
	}

	@GET
	@Timed
	public ExperimentStatus defaultAction() {
		return getExperimentStatus();
	}

	@Path("/start")
	@GET
	@Timed
	public ExperimentStatus start() {
		ExperimentStatus experimentStatus = getExperimentStatus();
		experimentStatus.setThreadAlreadyExecuting(!startBackgroundThreadSafely());
		return experimentStatus;
	}

	@Path("/status")
	@GET
	@Timed
	public ExperimentStatus getExperimentStatus() {
		ExperimentStatus experimentStatus = new ExperimentStatus();
		ExperimentContext context = experimentRunner.getContext();
		experimentStatus.setCycles(context.getCycles());
		experimentStatus.setIteration(context.getIteration());
		experimentStatus.setContinueExperimenting(experimentRunner.isContinueExperimenting());
		experimentStatus.setThreadAlreadyExecuting(backgroundThreadForRunner != null);
		experimentStatus.setExecutionTime(System.currentTimeMillis() - beginningExecutionTime);
		return experimentStatus;
	}

	@Path("/setCycles/{cycles}")
	@GET
	public ExperimentStatus setCycles(@PathParam("cycles") Integer cycles) {
		experimentRunner.getContext().setCycles(cycles);
		return getExperimentStatus();
	}

	@Path("/setContinueExperimenting/{continueExperimenting}")
	@GET
	public Boolean setContinueExperimenting(@PathParam("continueExperimenting") boolean continueExperimenting) {
		experimentRunner.setContinueExperimenting(continueExperimenting);
		return new Boolean(experimentRunner.isContinueExperimenting());
	}

	@Path("/stop")
	@GET
	public ExperimentStatus stop() {
		ExperimentStatus experimentStatus = getExperimentStatus();
		experimentRunner.setContinueExperimenting(false);
		experimentRunner.getContext().setCycles(0);
		return experimentStatus;
	}

	@Path("/lastExperimentResult")
	@GET
	public ExperimentResult getLastExperimentResult() {
		logger.info("Getting experiment result...");
		ExperimentResult result = analysisListener.getExperimentResult();
		logger.info(result.toString());
		return result;
	}

	@Path("/lastExperimentBestScoreGraph")
	@GET
	@Timed
	@Produces("image/png")
	public Response getLastExperimentBestScoreGraph() throws IOException {
		TimeSeries bestScoreData = (TimeSeries) experimentRunner.getContext()
				.getResource(AnalysisExperimentListener.BEST_SCORE_TIME_DATA_KEY);
		TimeSeriesCollection dataset = new TimeSeriesCollection(bestScoreData);
		JFreeChart timechart = ChartFactory.createTimeSeriesChart("Best Score Over Time", // Title
				"millisecond", // X-axis Label
				"Score for best Organism", // Y-axis Label
				dataset, // Dataset
				true, // Show legend
				true, // Use tooltips
				false // Generate URLs
		);
		SunPNGEncoderAdapter pngAdapter = new SunPNGEncoderAdapter();
		byte[] imageData = pngAdapter.encode(timechart.createBufferedImage(DEFAULT_CHART_WIDTH, DEFAULT_CHART_HEIGHT));
		return Response.ok(imageData).build();
	}

	private synchronized boolean startBackgroundThreadSafely() {
		if ((backgroundThreadForRunner != null) && (backgroundThreadForRunner.isAlive())) {
			return false;
		} else {
			beginningExecutionTime = System.currentTimeMillis();
			backgroundThreadForRunner = new Thread(experimentRunner);
			backgroundThreadForRunner.start();
			return true;
		}
	}

}
