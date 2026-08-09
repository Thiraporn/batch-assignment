package com.cp.assignment.miniproject.batch.reader;

import com.cp.assignment.miniproject.batch.model.MatchedRecord;
import org.springframework.batch.infrastructure.item.database.JdbcCursorItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class MatchedRecordReader {

    @Bean
    public JdbcCursorItemReader<MatchedRecord> matchedRecordItemReader(
            DataSource dataSource
    ) {

        return new JdbcCursorItemReaderBuilder<MatchedRecord>()
                .name("matchedRecordItemReader")
                .dataSource(dataSource)
                .sql("""
                        SELECT
                            order_number,
                            invoice_number,
                            list_a_amount,
                            list_b_amount,
                            error_message
                        FROM reconciliation_matched
                        ORDER BY id
                        """)
                .rowMapper((rs, rowNum) ->
                        new MatchedRecord(
                                rs.getString("order_number"),
                                rs.getString("invoice_number"),
                                rs.getBigDecimal("list_a_amount"),
                                rs.getBigDecimal("list_b_amount"),
                                rs.getString("error_message")
                        )
                )
                .build();
    }
}