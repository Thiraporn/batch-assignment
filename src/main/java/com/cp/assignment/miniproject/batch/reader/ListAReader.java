package com.cp.assignment.miniproject.batch.reader;

import com.cp.assignment.miniproject.batch.model.ListATransaction;
import com.cp.assignment.miniproject.common.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
@Slf4j
@Configuration
public class ListAReader {
    //private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");//eg. 2025-04-16
    //private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d/M/yyyy");//eg. 16/4/2025

    @Bean
    public FlatFileItemReader<ListATransaction> listAItemReader(@Value("${batch.input.list-a}") String filePath) {

            return new FlatFileItemReaderBuilder<ListATransaction>()
                    //FileSystemResource access local/ mapped  drive / UNC path
                    .name("listAReader")
                    .resource(new FileSystemResource(filePath))
                    .linesToSkip(0)
                    .delimited()
                    .delimiter(",")
                    .names(
                            "rowNumber",
                            "orderNumber",
                            "transactionDate",
                            "amount",
                            "fees1",
                            "fees2",
                            "netTotal",
                            "status"
                    )
                    .fieldSetMapper(field -> {
//                        String  d = field.readString("transactionDate");
//                        System.out.println( "READ A  ROW NUMBER = [" + field.readString("rowNumber")  + "]"  );
//                        System.out.println( "READ A  ORDER NUMBER = ["   + field.readString("orderNumber")   + "]"  );
//                        System.out.println( "READ A  DATE = ["   + d  + "]"  );

                        return ListATransaction.builder()
                                        .rowNumber(field.readString("rowNumber"))
                                        .orderNumber(field.readString("orderNumber"))
                                        //.transactionDate(  field.readString("transactionDate") != null  ? LocalDate.parse(  field.readString("transactionDate"),  DATE_FORMATTER)  : null )
                                        .transactionDate(CommonUtils.yyyyMMddParseDate(field.readString("transactionDate") ))
                                        .amount(field.readBigDecimal("amount"))
                                        .fees1(field.readBigDecimal("fees1"))
                                        .fees2(field.readBigDecimal("fees2"))
                                        .netTotal(field.readBigDecimal("netTotal"))
                                        .status(field.readString("status"))
                                        .build();
                            }
                    )
                    .build();



    }

}