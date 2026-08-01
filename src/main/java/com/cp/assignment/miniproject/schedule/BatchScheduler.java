package com.cp.assignment.miniproject.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "batch.scheduler.enabled",
        havingValue = "true"
)
public class BatchScheduler {

    private final JobOperator jobOperator;
    private final Job job;


    @Scheduled(cron = "${batch.scheduler.cron}")
    public void performBatchJob() throws Exception {
        log.info("\r\n#################### START MATCHING JOB List A vs List B (Scheduler) ####################");
        JobParameters params = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();

        jobOperator.start(job, params);
        log.info("\r\n#################### END MATCHING JOB List A vs List B (Scheduler) ####################");
    }
}


