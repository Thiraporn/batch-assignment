package com.cp.assignment.miniproject.batch.writer;


import com.cp.assignment.miniproject.batch.model.MatchedRecord;
import com.cp.assignment.miniproject.common.CommonUtils;
import org.springframework.batch.infrastructure.item.file.FlatFileItemWriter;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MatchedRecordCsvWriter {

    @Bean
    public FlatFileItemWriter<MatchedRecord> matchedRecordWriter(
            @Value("${batch.output.matched}") String filePath
    ) {

        return new FlatFileItemWriterBuilder<MatchedRecord>()
                .name("matchedRecordWriter")
                .resource(    new org.springframework.core.io.FileSystemResource(  filePath   ) )
                .shouldDeleteIfExists(true)
                .headerCallback(writer ->
                        writer.write( "orderNumber,invoiceNumber,listAAmount,listBAmount,errorMessage"
                        )
                )
                .lineAggregator(item ->
                        String.join(
                                ",",
                                CommonUtils.csv(item.getOrderNumber()),
                                CommonUtils.csv(item.getInvoiceNumber()),
                                CommonUtils.decimal(item.getListAAmount()),
                                CommonUtils.decimal(item.getListBAmount()),
                                CommonUtils.csv(item.getErrorMessage())
                        )
                )
                .build();
    }


}