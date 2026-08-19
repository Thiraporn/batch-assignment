package com.cp.assignment.miniproject.repository;


import com.cp.assignment.miniproject.batch.model.ErrorRecord;
import com.cp.assignment.miniproject.batch.model.ListATransaction;
import com.cp.assignment.miniproject.batch.model.ListBTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReconciliationRepository {

    private final JdbcTemplate jdbcTemplate;

    // ============================================================
    // INSERT LIST A
    // ============================================================

    public void insertListA(List<ListATransaction> items) {

        String sql = """
                INSERT INTO reconciliation_list_a
                (
                    row_number,
                    order_number,
                    transaction_date,
                    amount,
                    fees1,
                    fees2,
                    net_total,
                    status,
                    error_message
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.batchUpdate(
                sql,
                items,
                items.size(),
                (ps, item) -> {

                    ps.setString(1, item.getRowNumber());
                    ps.setString(2, item.getOrderNumber());
                    ps.setObject(3, item.getTransactionDate());
                    ps.setBigDecimal(4, item.getAmount());
                    ps.setBigDecimal(5, item.getFees1());
                    ps.setBigDecimal(6, item.getFees2());
                    ps.setBigDecimal(7, item.getNetTotal());
                    ps.setString(8, item.getStatus());
                    ps.setString(9, item.getErrorMessage());

                }
        );
    }

    // ============================================================
    // INSERT LIST B
    // ============================================================

    public void insertListB(List<ListBTransaction> items) {

        String sql = """
                INSERT INTO reconciliation_list_b
                (
                    row_number,
                    invoice_number,
                    transaction_date,
                    amount,
                    fees1,
                    fees2,
                    net_total,
                    card_number,
                    status,
                    error_message
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.batchUpdate(
                sql,
                items,
                items.size(),
                (ps, item) -> {

                    ps.setString(1, item.getRowNumber());
                    ps.setString(2, item.getInvoiceNumber());
                    ps.setObject(3, item.getTransactionDate());
                    ps.setBigDecimal(4, item.getAmount());
                    ps.setBigDecimal(5, item.getFees1());
                    ps.setBigDecimal(6, item.getFees2());
                    ps.setBigDecimal(7, item.getNetTotal());
                    ps.setString(8, item.getCardNumber());
                    ps.setString(9, item.getStatus());
                    ps.setString(10, item.getErrorMessage());
                }
        );
    }

    // ============================================================
    // CLEAN PREVIOUS RUN
    // ============================================================

    public void clearPreviousData() {

        jdbcTemplate.update("DELETE FROM reconciliation_list_a");
        jdbcTemplate.update("DELETE FROM reconciliation_list_b");
        jdbcTemplate.update("DELETE FROM reconciliation_matched");
        jdbcTemplate.update("DELETE FROM reconciliation_missing_in_a");
        jdbcTemplate.update("DELETE FROM reconciliation_missing_in_b");
    }

    // ============================================================
    // MATCHED
    // ============================================================

    public void generateMatchedRecords() {

        String sql = """
                INSERT INTO reconciliation_matched
                (
                    order_number,
                    invoice_number,
                    list_a_amount,
                    list_b_amount,
                    error_message
                )
                SELECT
                    a.order_number,
                    b.invoice_number,
                    a.amount,
                    b.amount,
                   CASE
                    WHEN NULLIF(a.error_message, '') IS NOT NULL
                         AND NULLIF(b.error_message, '') IS NOT NULL
                        THEN a.error_message || ';' || b.error_message
                
                    WHEN NULLIF(a.error_message, '') IS NOT NULL
                        THEN a.error_message
                
                    WHEN NULLIF(b.error_message, '') IS NOT NULL
                        THEN b.error_message
                
                    ELSE NULL
                   END
                FROM reconciliation_list_a a
                INNER JOIN reconciliation_list_b b ON a.order_number = b.invoice_number
                //need to fixed index and unique key for data table structure
                //where exists(select 1 from reconciliation_list_b b where a.order_number = b.invoice_number)
                """;

        jdbcTemplate.update(sql);
    }

    // ============================================================
    // MISSING IN A
    // B exists but A does not
    // ============================================================

    public void generateMissingInARecords() {

        String sql = """
                INSERT INTO reconciliation_missing_in_a
                (
                    invoice_number,
                    transaction_date,
                    amount,
                    error_message
                )
                SELECT
                    b.invoice_number,
                    b.transaction_date,
                    b.amount,
                    b.error_message
                FROM reconciliation_list_b b
                LEFT JOIN reconciliation_list_a a
                    ON a.order_number = b.invoice_number WHERE a.order_number IS NULL
                """;

        jdbcTemplate.update(sql);
    }

    // ============================================================
    // MISSING IN B
    // A exists but B does not
    // ============================================================

    public void generateMissingInBRecords() {

        String sql = """
                INSERT INTO reconciliation_missing_in_b
                (
                    order_number,
                    transaction_date,
                    amount,
                    error_message
                )
                SELECT
                    a.order_number,
                    a.transaction_date,
                    a.amount,
                    a.error_message
                FROM reconciliation_list_a a
                LEFT JOIN reconciliation_list_b b  ON a.order_number = b.invoice_number
                WHERE b.invoice_number IS NULL
                """;

        jdbcTemplate.update(sql);
    }

    public void markReconciledListA() {

        String sql = """
                UPDATE reconciliation_list_a a
                SET reconciled = TRUE
                WHERE EXISTS (
                    SELECT 1
                    FROM reconciliation_list_b b
                    WHERE b.invoice_number = a.order_number
                )
                """;

        jdbcTemplate.update(sql);
    }

    public void markReconciledListB() {

        String sql = """
                UPDATE reconciliation_list_b b
                SET reconciled = TRUE
                WHERE EXISTS (
                    SELECT 1
                    FROM reconciliation_list_a a
                    WHERE a.order_number = b.invoice_number
                )
                """;

        jdbcTemplate.update(sql);
    }


    //not use current
    public void saveError(
            String sourceFile,
            String rowNumber,
            String reference,
            String errorMessage
    ) {
        jdbcTemplate.update("""
                        INSERT INTO reconciliation_error
                            (source_file, row_number, reference, error_message)
                        VALUES (?, ?, ?, ?)
                        """,
                sourceFile,
                rowNumber,
                reference,
                errorMessage
        );
    }
    //not use current
    public void clearErrorRecords() {
        jdbcTemplate.update("DELETE FROM reconciliation_error");
    }
    //not use current
    public List<ErrorRecord> findAllErrors() {
        return jdbcTemplate.query("""
                        SELECT
                            source_file,
                            row_number,
                            reference,
                            error_message
                        FROM reconciliation_error
                        ORDER BY id
                        """,
                (rs, rowNum) -> ErrorRecord.builder()
                        .sourceFile(rs.getString("source_file"))
                        .rowNumber(rs.getString("row_number"))
                        .reference(rs.getString("reference"))
                        .errorMessage(rs.getString("error_message"))
                        .build()
        );
    }
}