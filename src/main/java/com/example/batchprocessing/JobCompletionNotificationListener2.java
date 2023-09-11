package com.example.batchprocessing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;


@Component
public class JobCompletionNotificationListener2 implements JobExecutionListener {

	private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener2.class);
	//EDIInputFactory factory = EDIInputFactory.newFactory();
	String basePath = "src/main/resources/";
	String filename = "sample-data-out.csv"; // "IFTSTA_1048659__20230719101110.edi"
	String filePath = basePath + filename;
	Set<String> events = new TreeSet<String>();

	
	

	@Override
	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("!!! 2nd JOB FINISHED! Time to verify the results");

			try (InputStream stream = new FileInputStream(filePath)) {
				
				/*
				 * EDIStreamReader reader = factory.createEDIStreamReader(stream);
				 * EDIStreamEvent event;
				 * 
				 * while (reader.hasNext()) { event = reader.next(); events.add(event.name()); }
				 */
				 
				// .forEach(person -> log.info("Found <{{}}> in the database.", person));
				//System.out.println("Stream print :: "+stream.readAllBytes().toString()); 
				new BufferedReader(new InputStreamReader(stream))
						   .lines()
						   .forEach(line -> log.info("Found <{}> in the file.", line));
				 events.add(stream.toString());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} /* catch (EDIStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} */
		}

		//events.stream().forEach(x -> System.out.print(x));
	}




	@Override
	public void beforeJob(JobExecution jobExecution) {
		// TODO Auto-generated method stub
		
	}

}
