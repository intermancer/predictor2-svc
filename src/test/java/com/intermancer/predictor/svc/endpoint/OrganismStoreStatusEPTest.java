package com.intermancer.predictor.svc.endpoint;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import static org.junit.Assert.*;

import com.intermancer.predictor.experiment.ExperimentPrimeRunner;
import com.intermancer.predictor.svc.config.PredictorSvcConfig;

public class OrganismStoreStatusEPTest {

	private static final String TEST_STORE_PATH = "src/test/resources/com/intermancer/predictor/test/data/dump";

	@Test
	public void testDumpTop() throws Exception {
		clearTestData();

		ExperimentPrimeRunner experimentRunner = new ExperimentPrimeRunner();
		experimentRunner.init();
		PredictorSvcConfig config = new PredictorSvcConfig();
		config.setOrganismStorePath(TEST_STORE_PATH);
		OrganismStoreStatusEP endpoint = new OrganismStoreStatusEP(experimentRunner, config);
		endpoint.dumpTop(2);

		int fileCount = 0;
		Path testDirectoryPath = Paths.get(TEST_STORE_PATH);
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(testDirectoryPath)) {
			for (@SuppressWarnings("unused")
			Path file : stream) {
				fileCount++;
			}
		}
		assertEquals(fileCount, 2);
	}

	private void clearTestData() throws Exception {
		Path testDirectoryPath = Paths.get(TEST_STORE_PATH);
		Files.createDirectories(testDirectoryPath);
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(testDirectoryPath)) {
			for (Path file : stream) {
				Files.delete(file);
			}
		}
	}

}
