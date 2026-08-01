package com.cp.assignment.miniproject.batch.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
@RequestMapping("/api/v1/batch")
@RequiredArgsConstructor
public class BatchController {

    private final JobOperator jobOperator;
    private final  Job job;

    @PostMapping("/run")
    public ResponseEntity<String> runBatch() throws Exception {
        log.info("\r\n#################### START MATCHING JOB List A vs List B (Manually) ####################");
        JobParameters params = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();

        jobOperator.start(job, params);

        log.info("\r\n#################### END MATCHING JOB List A vs List B (Manually) ####################");

        return ResponseEntity.ok("Batch job started");
    }
}