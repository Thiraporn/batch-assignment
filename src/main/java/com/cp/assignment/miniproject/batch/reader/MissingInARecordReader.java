package com.cp.assignment.miniproject.batch.reader;

import com.cp.assignment.miniproject.batch.model.MissingInARecord;
import org.springframework.batch.infrastructure.item.database.JdbcCursorItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.time.LocalDate;

@Configuration
public class MissingInARecordReader {

    @Bean
    public JdbcCursorItemReader<MissingInARecord> missingInAReader(
            DataSource dataSource
    ) {

        return new JdbcCursorItemReaderBuilder<MissingInARecord>()
                .name("missingInAReader")
                .dataSource(dataSource)
                .sql("""
                    SELECT
                        invoice_number,
                        transaction_date,
                        amount,
                        error_message
                    FROM reconciliation_missing_in_a
                    ORDER BY id
                    """)
                .rowMapper((rs, rowNum) ->
                        new MissingInARecord(
                                rs.getString("invoice_number"),
                                rs.getObject(
                                        "transaction_date",
                                        LocalDate.class
                                ),
                                rs.getBigDecimal("amount"),
                                rs.getString("error_message")
                        )
                )
                .build();
    }
}