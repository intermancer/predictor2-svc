package com.intermancer.predictor.svc.endpoint;

import java.io.BufferedWriter;
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

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intermancer.predictor.experiment.ExperimentPrimeRunner;
import com.intermancer.predictor.organism.store.OrganismStoreRecord;
import com.intermancer.predictor.svc.config.PredictorSvcConfig;
import com.intermancer.predictor.svc.dom.StoreStatus;

@Path("/store")
@Produces(MediaType.APPLICATION_JSON)
public class OrganismStoreStatusEP {

	private static final Charset charset = Charset.forName("US-ASCII");

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
		StoreStatus storeStatus = new StoreStatus(experimentRunner.getOrganismStore());
		return storeStatus;
	}

	@Path("/dumpTop/{numberToDump}")
	@GET
	@Timed
	public List<OrganismStoreRecord> dumpTop(@PathParam("numberToDump") int numberToDump) throws Exception {
		List<OrganismStoreRecord> topOrganisms = new ArrayList<OrganismStoreRecord>();
		String dumptime = Long.toString(System.currentTimeMillis());
		for (int i = 0; i < numberToDump; i++) {
			OrganismStoreRecord record = experimentRunner.getOrganismStore().findByIndex(i);
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

}
