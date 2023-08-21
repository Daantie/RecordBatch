package com.example.rabobankassignment.web;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("jobs")
public class JobRestController {
    private final JobLauncher jobLauncher;
    private final Job csvJob;
    private final Job xmlJob;

    public JobRestController(JobLauncher jobLauncher, @Qualifier("csvBatchJob") Job csvJob, @Qualifier("xmlBatchJob") Job xmlJob) {
        this.jobLauncher = jobLauncher;
        this.csvJob = csvJob;
        this.xmlJob = xmlJob;
    }

    @GetMapping("/startCsvBatch")
    public ResponseEntity<HttpStatus> startCsvBatch() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        jobLauncher.run(csvJob, new JobParameters());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/startXmlBatch")
    public ResponseEntity<HttpStatus> startXmlBatch() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        jobLauncher.run(xmlJob, new JobParameters());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
