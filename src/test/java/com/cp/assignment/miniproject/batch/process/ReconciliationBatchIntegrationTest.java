package com.cp.assignment.miniproject.batch.process;

import com.cp.assignment.miniproject.batch.model.ListBTransaction;
import com.cp.assignment.miniproject.batch.model.ReconciliationResult;
import com.cp.assignment.miniproject.batch.service.ListACacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.JobOperatorTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Import;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.batch.core.launch.JobOperator;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@SpringBatchTest
@Import(ReconciliationBatchIntegrationTest.TestBatchConfig.class)
class ReconciliationBatchIntegrationTest {

    @Autowired
    private JobOperator jobOperator;

    @Autowired
    private Job reconciliationJob;

    /*   Example chunk = 5    */
     /*List B

        1  → 2696111
                2  → 2696112
                3  → 2696113
                4  → 2696114
                5  → 2696115
                ↓
            CHUNK 1    check point #1
                ↓
        COMMIT ✅
                6  → 2696116
                7  → 2696117  ← TestBatchConfig ทำให้ ERROR
                8  → 2696118
                9  → 2696119
                10 → 2696120
                ↓
            CHUNK 2    check point #2
                    ↓
            ERROR
               ↓
            ROLLBACK ❌*

            จากนั้น restart(): start from last check point
             6 7 8 9 10
             ↓
            COMMIT ✅
           Job COMPLETED

         */
    @Test
    void testRestart() throws Exception {

        JobParameters jobParameters = new JobParametersBuilder().addString("testId", "restart-test-001").toJobParameters();

        // =========================
        // RUN #1
        // =========================
        JobExecution firstExecution = jobOperator.start(reconciliationJob, jobParameters);
        System.out.println("RUN #1 STATUS = " + firstExecution.getStatus());
        // ต้อง FAILED
        assertEquals(BatchStatus.FAILED, firstExecution.getStatus());


        // =========================
        // RUN #2 - RESTART
        // =========================
        JobExecution secondExecution = jobOperator.start(reconciliationJob, jobParameters);
        System.out.println("RUN #2 STATUS = " + secondExecution.getStatus());
        // ต้อง COMPLETED
        assertEquals(BatchStatus.COMPLETED, secondExecution.getStatus());
    }


    // =========================================================
    // Test configuration
    // =========================================================
    @TestConfiguration
    static class TestBatchConfig {
        AtomicBoolean firstRun = new AtomicBoolean(true);
        AtomicInteger count = new AtomicInteger();

        @Bean
        @Primary
        ReconciliationProcessor testReconciliationProcessor(ListACacheService listACacheService) {
            return new ReconciliationProcessor(listACacheService) {

                @Override
                public ReconciliationResult process(
                        ListBTransaction item) {
                    // เก็บว่า Processor ถูกเรียกกับ record ไหน
                    int current = count.incrementAndGet();
                    System.out.println("PROCESSING #" + current + "PROCESSING invoice = " + item.getInvoiceNumber());
                    // จำลอง error ที่ record 7
                    // TEST ONLY
                    // Record 7 = invoice 2696117
                    if (firstRun.get() && "2696117".equals(item.getInvoiceNumber())) {
                        System.out.println("!!! TEST ERROR at invoice = " + item.getInvoiceNumber());
                        // ให้ Restart ครั้งต่อไปผ่าน
                        firstRun.set(false);
                        System.out.println("...............Waiting for resume a job = " + item.getInvoiceNumber());
                        throw new RuntimeException("TEST ERROR");
                    }

                    return super.process(item);
                }
            };
        }

    }
}

