package com.cp.assignment.miniproject.batch.config;

import com.cp.assignment.miniproject.batch.model.*;
import com.cp.assignment.miniproject.batch.process.ListAValidationProcessor;
import com.cp.assignment.miniproject.batch.process.ListBValidationProcessor;

import com.cp.assignment.miniproject.batch.writer.ErrorWriter;
import com.cp.assignment.miniproject.batch.writer.ListAWriter;
import com.cp.assignment.miniproject.batch.writer.ListBWriter;
import com.cp.assignment.miniproject.repository.ReconciliationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.FlatFileItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcCursorItemReader;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeParseException;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class ReconciliationBatchConfiguration {

    private final JobRepository jobRepository;

    // private final PlatformTransactionManager transactionManager;

    private final FlatFileItemReader<ListATransaction> listAItemReader;

    private final FlatFileItemReader<ListBTransaction> listBItemReader;

    private final ListAValidationProcessor listAValidationProcessor;

    private final ListBValidationProcessor listBValidationProcessor;

    private final ListAWriter listAWriter;

    private final ListBWriter listBWriter;

    private final ReconciliationRepository repository;
    private final ReconciliationSkipListener skipListener;
    private final ReconciliationSkipPolicy skipPolicy;


    private final ErrorWriter errorWriter;

    private final ErrorFileManager errorFileManager;


    // ============================================================
    // JOB
    // ============================================================

    @Bean
    public Job reconciliationJob(@Qualifier("clearPreviousDataStep") Step clearPreviousDataStep, @Qualifier("loadListAStep") Step loadListAStep, @Qualifier("loadListBStep") Step loadListBStep, @Qualifier("reconciliationStep") Step reconciliationStep, @Qualifier("exportMatchedStep") Step exportMatchedStep, @Qualifier("exportMissingInAStep") Step exportMissingInAStep, @Qualifier("exportMissingInBStep") Step exportMissingInBStep) {

        return new JobBuilder("reconciliationJob", jobRepository).start(clearPreviousDataStep).next(loadListAStep).next(loadListBStep).next(reconciliationStep).next(exportMatchedStep).next(exportMissingInAStep).next(exportMissingInBStep).build();
    }


    // ============================================================
    // STEP 0
    // Clear previous run
    // ============================================================

    @Bean
    public Step clearPreviousDataStep() {
        //JobRepository คือ ที่ Spring Batch ใช้เก็บสถานะและข้อมูลการทำงานของ Job/Step เพื่อให้รู้ว่า Job ทำถึงไหนแล้ว และสามารถ restart/resume ได้
        //Spring Batch จึงรู้ว่า Step 1 ไม่ต้องทำใหม่ และ Step 2 สามารถ restart ได้
        return new StepBuilder("clearPreviousDataStep", jobRepository)
                //Tasklets : แต่ละขั้นตอน (Step) จะทำงานเพียงขั้นตอนเดียว (Single) หลังจากนั้นค่อยทำขั้นตอนถัดไป (Next)
                .tasklet((contribution, chunkContext) -> {
                    log.info("Clearing previous reconciliation data");

                    repository.clearPreviousData();

                    //errorWriter.close();

                    // ลบไฟล์เก่าที่นี่
                    // แล้วให้ ErrorWriter สร้างใหม่เมื่อเกิด error
                    //Files.deleteIfExists(Paths.get(errorFilePath));
                    errorFileManager.deletePreviousErrorFile();
                    errorFileManager.createEmptyErrorFile();
                    // สร้าง Error_Records.csv พร้อม header
                    //errorFileManager.createEmptyErrorFile(); //empty file
                    //errorWriter.createEmptyFile();//empty file with header

                    return RepeatStatus.FINISHED;
                }).build();
    }


    // ============================================================
    // STEP 1
    // Load List A
    // ============================================================

    @Bean
    public Step loadListAStep() {
        log.info("----Step 2 : loadListAStep() Read List A -----------");
        return new StepBuilder("loadListAStep", jobRepository)
                //split data into small group
                .<ListATransaction, ListATransaction>chunk(1000)
                // 1. read source data - split data into small group
                .reader(listAItemReader)
                // 2. validate data
                .processor(listAValidationProcessor)
                // 3. insert List A into data base
                .writer(listAWriter)

                // 4. skip error
                .faultTolerant()

                // SkipPolicy decides which errors can be skipped
                .skipPolicy(skipPolicy)// Invalid date


                .skipLimit(100).listener(skipListener)


                //Spring Batch ไม่ได้รู้ว่า ErrorWriter ต้องถูกจัดการ lifecycle
                //ต้อง register กับ Step ด้วย .stream(...) เพื่อเรียก close() ไม่งั้นจะเกิดปัญหาเรื่อง file is in use
                //.stream(errorWriter)
                .build();
    }


    // ============================================================
    // STEP 2
    // Load List B
    // ============================================================

    @Bean
    public Step loadListBStep() {
        log.info("----Step 2 : loadListAStep() Read List B -----------");
        return new StepBuilder("loadListBStep", jobRepository)
                //split data into small group
                .<ListBTransaction, ListBTransaction>chunk(5)
                // 1. read source data - split data into small group
                .reader(listBItemReader)
                // 2. validate data
                .processor(listBValidationProcessor)
                // 3. insert List B into data base
                .writer(listBWriter)

                // 4. skip error
                .faultTolerant()

                // SkipPolicy decides which errors can be skipped
                .skipPolicy(skipPolicy)// Invalid date


                .skipLimit(100).listener(skipListener)



                //Spring Batch ไม่ได้รู้ว่า ErrorWriter ต้องถูกจัดการ lifecycle
                //ต้อง register กับ Step ด้วย .stream(...) เพื่อเรียก close() ไม่งั้นจะเกิดปัญหาเรื่อง file is in use

                //.stream(errorWriter)
                .build();
    }


    // ============================================================
    // STEP 3
    // DB reconciliation
    // ============================================================

    @Bean
    public Step reconciliationStep() {
        log.info("----Step 3 : Reconcile data -----------");
        return new StepBuilder("reconciliationStep", jobRepository).tasklet((contribution, chunkContext) -> {
            log.info("Update MATCHED records");
            repository.markReconciledListA();
            repository.markReconciledListB();

            log.info("Generating MATCHED records");
            repository.generateMatchedRecords();

            log.info("Generating MISSING_IN_A records");
            repository.generateMissingInARecords();

            log.info("Generating MISSING_IN_B records");
            repository.generateMissingInBRecords();

            return RepeatStatus.FINISHED;
        }).build();
    }


    // ============================================================
    // STEP 4
    // Export MATCHED
    // ============================================================

    @Bean
    public Step exportMatchedStep(JdbcCursorItemReader<MatchedRecord> matchedRecordItemReader, FlatFileItemWriter<MatchedRecord> matchedRecordWriter) {
        log.info("----Step 4 : Export Match file -----------");
        return new StepBuilder("exportMatchedStep", jobRepository).<MatchedRecord, MatchedRecord>chunk(1000).reader(matchedRecordItemReader).writer(matchedRecordWriter).build();
    }


    // ============================================================
    // STEP 5
    // Export MISSING IN A
    // ============================================================

    @Bean
    public Step exportMissingInAStep(JdbcCursorItemReader<MissingInARecord> missingInAReader, FlatFileItemWriter<MissingInARecord> missingInAWriter) {
        log.info("----Step 5 : Export Missing in A file -----------");
        return new StepBuilder("exportMissingInAStep", jobRepository).<MissingInARecord, MissingInARecord>chunk(1000).reader(missingInAReader).writer(missingInAWriter).build();
    }

    // ============================================================
    // STEP 6
    // Export MISSING IN B
    // ============================================================

    @Bean
    public Step exportMissingInBStep(JdbcCursorItemReader<MissingInBRecord> missingInBReader, FlatFileItemWriter<MissingInBRecord> missingInBWriter) {
        log.info("----Step 6 : Export Missing in B file -----------");
        return new StepBuilder("exportMissingInBStep", jobRepository).<MissingInBRecord, MissingInBRecord>chunk(1000).reader(missingInBReader).writer(missingInBWriter).build();
    }


}