package com.example.batchprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class BatchProcessingApplication {
	
	static Logger log = LoggerFactory.getLogger(BatchProcessingApplication.class);
	
	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext ctx = SpringApplication.run(BatchProcessingApplication.class, args);
		String jobToLaunch = "";
		if(args.length > 0 && args[0].contains("Edi"))
			jobToLaunch = args[0];
		JobLauncher jobLauncher = (JobLauncher) ctx.getBean("jobLauncher");
		Job imporUserJobBean = (Job) ctx.getBean("importUserJob");
		Job exporUserJobBean = (Job) ctx.getBean("exportUserJob");
		if(!jobToLaunch.isEmpty() && jobToLaunch.contains("Import")) {
			jobLauncher.run(imporUserJobBean, new JobParameters());
		}
		else if(!jobToLaunch.isEmpty() && jobToLaunch.contains("Export")) {
			jobLauncher.run(exporUserJobBean, new JobParameters());
		} else {
			jobLauncher.run(imporUserJobBean, new JobParameters());
			jobLauncher.run(exporUserJobBean, new JobParameters());
		}
		
		
		System.exit(SpringApplication.exit(ctx));
	}
}
