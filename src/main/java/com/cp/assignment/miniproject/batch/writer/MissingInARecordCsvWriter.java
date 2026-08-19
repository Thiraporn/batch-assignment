package com.cp.assignment.miniproject.batch.writer;

import com.cp.assignment.miniproject.batch.model.MissingInARecord;
import com.cp.assignment.miniproject.common.CommonUtils;
import org.springframework.batch.infrastructure.item.file.FlatFileItemWriter;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.math.BigDecimal;

@Configuration
public class MissingInARecordCsvWriter {

    @Bean
    public FlatFileItemWriter<MissingInARecord> missingInAWriter(
            @Value("${batch.output.missing-in-a}") String filePath
    ) {

        return new FlatFileItemWriterBuilder<MissingInARecord>()
                .name("missingInAWriter")
                .resource(
                        new FileSystemResource(filePath)
                )
                .shouldDeleteIfExists(true)
                .headerCallback(writer ->
                        writer.write(
                                "invoiceNumber,transactionDate,amount,errorMessage"
                        )
                )
                .lineAggregator(item ->
                        String.join(
                                ",",
                                CommonUtils.csv(item.getInvoiceNumber()),
                                item.getTransactionDate() == null ? "" : item.getTransactionDate().toString(),
                                item.getAmount() == null ? "" : item.getAmount().toPlainString(),
                                CommonUtils.csv(item.getErrorMessage())
                        )
                )
                .build();

    }


}