package com.example.batchprocessing;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class JobLauncherService {

    private final JobLauncher jobLauncher;
    
    private final Job job;

    @Autowired
    public JobLauncherService(JobLauncher jobLauncher, @Qualifier("multipleStepJob") Job job) {
        this.jobLauncher = jobLauncher;
        this.job = job;
    }

    public void runJob() {
        try {
            final JobExecution jobExecution = jobLauncher.run(job, new JobParameters());
            System.out.println("Job Status : " + jobExecution.getStatus());
            System.out.println("Job completed");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Job failed");
        }
    }
}