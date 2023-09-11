package com.example.batchprocessing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;

import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.AssertFile;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBatchTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { BatchProcessingApplication.class })
@SpringBootTest
public class SpringBatchImportTest {

	private static final String TEST_OUTPUT = "src/test/resources/sample-data-output.csv";

	private static final String EXPECTED_OUTPUT = "src/test/resources/expected-data-output.csv";

	private static final String TEST_INPUT = "src/test/resources/sample-data-input.csv";

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private JobRepositoryTestUtils jobRepositoryTestUtils;
	
	
	// override method with name of your job bean
	@Bean
	public JobLauncherTestUtils getJobLauncherTestUtils() {

	    return new JobLauncherTestUtils() {
	        @Override
	        @Autowired
	        public void setJob(@Qualifier("importUserJob") Job job) {
	            super.setJob(job);
	        }
	    };
	}
	
	@Autowired
	@Qualifier(value = "importUserJob")
	private Job importJob;
	
	@Autowired
	@Qualifier(value = "exportUserJob")
	private Job exportJob;

	@AfterEach
	public void cleanUp() {
		jobRepositoryTestUtils.removeJobExecutions();
	}

	@Before 
	public void setup() {
		jobLauncherTestUtils.setJob(importJob);
	}
	 
	@Test
	public void givenReferenceOutput_whenJobExecuted_thenSuccess() throws Exception {
		// given
		FileSystemResource expectedResult = new FileSystemResource(EXPECTED_OUTPUT);
		FileSystemResource actualResult = new FileSystemResource(TEST_OUTPUT);

		// when
		
		JobExecution jobExecution = jobLauncherTestUtils.launchJob();
		JobInstance actualJobInstance = jobExecution.getJobInstance();
		ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

		// then
		assertEquals("importUserJob", actualJobInstance.getJobName());
		assertEquals("COMPLETED", actualJobExitStatus.getExitCode());
		AssertFile.assertFileEquals(expectedResult, actualResult);
	}

	@Test
	public void givenReferenceOutput_whenStep1Executed_thenSuccess() throws Exception {

		// given
		FileSystemResource expectedResult = new FileSystemResource(EXPECTED_OUTPUT);
		FileSystemResource actualResult = new FileSystemResource(TEST_OUTPUT);

		// when
		jobLauncherTestUtils.setJob(importJob);
		JobExecution jobExecution = jobLauncherTestUtils.launchStep("step1");
		Collection<StepExecution> actualStepExecutions = jobExecution.getStepExecutions();
		ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

		// then
		assertEquals(1, actualStepExecutions.size());
		assertEquals("COMPLETED", actualJobExitStatus.getExitCode());
		AssertFile.assertFileEquals(expectedResult, actualResult);
	}

	
}
