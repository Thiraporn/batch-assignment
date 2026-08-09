package com.cp.assignment.miniproject.batch.reader;

import com.cp.assignment.miniproject.batch.model.MissingInBRecord;
import org.springframework.batch.infrastructure.item.database.JdbcCursorItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.time.LocalDate;

@Configuration
public class MissingInBRecordReader {
    @Bean
    public JdbcCursorItemReader<MissingInBRecord> missingInBReader(
            DataSource dataSource
    ) {

        return new JdbcCursorItemReaderBuilder<MissingInBRecord>()
                .name("missingInBReader")
                .dataSource(dataSource)
                .sql("""
                    SELECT
                        order_number,
                        transaction_date,
                        amount,
                        error_message
                    FROM reconciliation_missing_in_b
                    ORDER BY id
                    """)
                .rowMapper((rs, rowNum) ->
                        new MissingInBRecord(
                                rs.getString("order_number"),
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