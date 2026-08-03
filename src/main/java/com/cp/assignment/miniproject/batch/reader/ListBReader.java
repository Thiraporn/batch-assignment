package com.cp.assignment.miniproject.batch.reader;

import com.cp.assignment.miniproject.batch.model.ListBTransaction;
import com.cp.assignment.miniproject.common.CommonUtils;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Configuration
public class ListBReader {
    @Bean
    public FlatFileItemReader<ListBTransaction> listBItemReader(
            @Value("${batch.input.list-b}") String filePath
    ) {

        return new FlatFileItemReaderBuilder<ListBTransaction>()
                .name("listBReader")
                //FileSystemResource access local/ mapped  drive / UNC path
                .resource(new FileSystemResource(filePath))
                //.linesToSkip(1)
                //.linesToSkip(0)
                .delimited()
                .delimiter(",")
                .names(
                        "rowNumber",
                        "invoiceNumber",
                        "transactionDate",
                        "amount",
                        "fees1",
                        "fees2",
                        "netTotal",
                        "cardNumber",
                        "status"
                )

                .fieldSetMapper(field -> {
//                    String  d = field.readString("transactionDate");
//                    System.out.println( "READ B  ROW NUMBER = [" + field.readString("rowNumber")  + "]"  );
//                    System.out.println( "READ B  INVOICE = ["   + field.readString("invoiceNumber")   + "]"  );
//                    System.out.println( "READ B  DATE = ["   + d  + "]"  );
                    return ListBTransaction.builder()
                            .rowNumber(field.readString("rowNumber"))
                            .invoiceNumber(field.readString("invoiceNumber"))
                            .transactionDate(CommonUtils.ddMMyyyyParseDate(field.readString("transactionDate") ))
                            .amount(CommonUtils.parseDecimal(field.readString("amount")))
                            .fees1(CommonUtils.parseDecimal(field.readString("fees1")))
                            .fees2(CommonUtils.parseDecimal(field.readString("fees2")))
                            .netTotal(CommonUtils.parseDecimal(field.readString("netTotal")))
                            .cardNumber( field.readString("cardNumber"))
                            .status(field.readString("status"))
                            .build();
                })
                .build();
    }

}