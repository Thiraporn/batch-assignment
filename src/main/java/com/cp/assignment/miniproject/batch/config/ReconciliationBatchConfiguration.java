package com.cp.assignment.miniproject.batch.config;

import com.cp.assignment.miniproject.batch.model.ListATransaction;
import com.cp.assignment.miniproject.batch.model.ListBTransaction;
import com.cp.assignment.miniproject.batch.model.ReconciliationResult;
import com.cp.assignment.miniproject.batch.process.ListACacheProcessor;
import com.cp.assignment.miniproject.batch.process.MapReconciliationProcessor;
import com.cp.assignment.miniproject.batch.process.ReconciliationProcessor;
import com.cp.assignment.miniproject.batch.service.ListACacheService;
import com.cp.assignment.miniproject.batch.service.MapListACacheService;
import com.cp.assignment.miniproject.batch.writer.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class ReconciliationBatchConfiguration {

    private final JobRepository jobRepository;

    private final FlatFileItemReader<ListATransaction> listAItemReader;

    private final FlatFileItemReader<ListBTransaction>  listBItemReader;

    private final ListACacheProcessor listACacheProcessor;

    private final ReconciliationProcessor reconciliationProcessor;

    private final ReconciliationWriter reconciliationWriter;

    private final MapReconciliationProcessor mapReconciliationProcessor;
    private final ListACacheService listACacheService;

    private final MapListACacheService mapListACacheService;

    private final MissingInBCsvWriter missingInBWriter;

    private final MatchedCsvWriter matchedWriter;
    private final MissingInACsvWriter missingInAWriter;
    private final ErrorWriter errorWriter;

    //main (1 ่job  run 3 steps)
    @Bean
    public Job reconciliationJob() {
        return new JobBuilder( "reconciliationJob",   jobRepository  )
                .start(loadListAStep())      //1. Reading data in List A
                .next(reconciliationStep())  //2. Matching in List B
                .next(writeMissingInBStep()) //3. Writing output
                //.next(writeListMissingInBStep()) //3. Writing output ----> resolve duplicate key hash map for A
                .build();
    }


    /**
     * Step 1 :
     * Read List A
     * ↓
     * ListACacheProcessor
     * ↓
     * HashMap
     */
    @Bean
    public Step loadListAStep() {
        log.info("----Step 1 : loadListAStep() Read List A and keep them as HashMap -----------");
        return new StepBuilder(  "loadListAStep",  jobRepository   )
                .<ListATransaction, ListATransaction>chunk(5)//split data into small group
                // 1. add item HashMap
                //source data - split data into small group
                .reader(listAItemReader)
                //add curr group into cache
                .processor(listACacheProcessor)
                // 2. prepare data for DB (if need to save data into data source)
                // No physical output.
                // Data is stored in cache.
                .writer(items -> {
                })

                .build();
    }


    /**
     * Step 2
     * Read List B
     * ↓
     * ReconciliationProcessor
     * ↓
     * ReconciliationWriter
     * MATCHED
     * MISSING_IN_A
     * ERROR
     */
    @Bean
    public Step reconciliationStep() {
        //log.info("Step 1 : loadListAStep ", ex.getMessage(), ex);
        log.info("----Step 2 : reconciliationStep - loop Read List B ,and match with List A including write file-----------");
        return new StepBuilder(
                "reconciliationStep",
                jobRepository
        )
                .<ListBTransaction, ReconciliationResult>chunk(1000)
                // ไม่มี skip()
                //.skip(RuntimeException.class)
                .reader(listBItemReader)            //Reading List B
                .processor(reconciliationProcessor) //Matching in List B
                .writer(reconciliationWriter)      //Writhing Output

                // Register nested writers as ItemStreams -
                // Tell Spring Batch that matchedWriter, missingInAWriter, errorWriter is ItemStream
                // ItemStream need some step to take care lifecycle with open() write() update() close()???
                .stream(matchedWriter)
                .stream(missingInAWriter)
                //.stream(errorWriter)
                .build();
    }
    /**
     * Step 3
     * Anything remaining in List A cache
     * was never matched by List B.
     * ↓
     * Therefore:
     * ↓
     * remaining cache = MISSING_IN_B
     */
    @Bean
    public Step writeMissingInBStep() {
        log.info("----Step 3 : writeMissingInBStep - remaining cache = MISSING_IN_B -----------");
        return new StepBuilder(  "writeMissingInBStep",    jobRepository   )
                //Tasklets : แต่ละขั้นตอน (Step) จะทำงานเพียงขั้นตอนเดียว (Single) หลังจากนั้นค่อยทำขั้นตอนถัดไป (Next)
                .tasklet(  (contribution, chunkContext) -> {
                            log.info("----listA remaining -----------");
                            System.out.println( "list A  remaining = [" + listACacheService.size() + "]"  );
                            listACacheService
                                    .getRemaining()
                                    .values()
                                    .forEach(listA -> {// Anything remaining in List A cache was never matched by List B.
                                        // Validate List A
                                        ReconciliationResult result =  reconciliationProcessor.processMissingInB(listA);

                                        try {
                                            missingInBWriter.write(  new Chunk<>(  List.of(result) )  );
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
                                    });
                            return RepeatStatus.FINISHED;
                        }
                )
                .stream(missingInBWriter)
                .build();
    }

    //resolve duplicate A
//    @Bean
//    public Step writeListMissingInBStep() {
//        return new StepBuilder("writeListMissingInBStep", jobRepository).tasklet((contribution, chunkContext) -> {
//
//                    Map<String, List<ListATransaction>> remaining =  mapListACacheService.getRemaining();
//                    log.info("----listA remaining -----------");
//                    System.out.println( "list A  remaining = [" + mapListACacheService.size() + "]"  );
//
//                    for (List<ListATransaction> transactions : remaining.values()) {
//                        for (ListATransaction listA : transactions) {
//
//                            ReconciliationResult result =  mapReconciliationProcessor.processMissingInB(listA);
//
//                            try {
//                                missingInBWriter.write(  new Chunk<>(  List.of(result) )  );
//                            } catch (Exception e) {
//                                throw new RuntimeException(e);
//                            }
//
//                        }
//                    }
//
//                    return RepeatStatus.FINISHED;
//                })
//                .stream(missingInBWriter)
//                .build();
//    }




}