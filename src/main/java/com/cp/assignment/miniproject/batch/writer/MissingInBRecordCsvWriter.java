package com.cp.assignment.miniproject.batch.writer;

import com.cp.assignment.miniproject.batch.model.MissingInBRecord;
import com.cp.assignment.miniproject.common.CommonUtils;
import org.springframework.batch.infrastructure.item.file.FlatFileItemWriter;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class MissingInBRecordCsvWriter {

    @Bean
    public FlatFileItemWriter<MissingInBRecord> missingInBWriter(
            @Value("${batch.output.missing-in-b}") String filePath
    ) {

        return new FlatFileItemWriterBuilder<MissingInBRecord>()
                .name("missingInBWriter")
                .resource(
                        new FileSystemResource(filePath)
                )
                .shouldDeleteIfExists(true)
                .headerCallback(writer ->
                        writer.write(
                                "orderNumber,transactionDate,amount,errorMessage"
                        )
                )
                .lineAggregator(item ->
                        String.join(
                                ",",
                                CommonUtils.csv(item.getOrderNumber()),
                                item.getTransactionDate() == null ? "" : item.getTransactionDate().toString(),
                                item.getAmount() == null ? "" : item.getAmount().toPlainString(),
                                CommonUtils.csv(item.getErrorMessage())
                        )
                )
                .build();

    }

}