package com.cp.assignment.miniproject.repository;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class ReconciliationRepositoryTest {
    @Autowired
    private ReconciliationRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    //Case 1: Match สำเร็จ
    @Test
    void shouldGenerateMatchedRecordWhenOrderNumberMatchesInvoiceNumber() {

        // Arrange
        insertListA("2696115", "1000.00", null);
        insertListB("2696115", "1000.00", null);

        // Act
        repository.generateMatchedRecords();

        // Assert
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM reconciliation_matched
                WHERE order_number = ?
                """,
                Integer.class,
                "2696115"
        );

        assertEquals(1, count);
    }

    //Case 2: Match แต่ List A มี error
   /* List A
    2696115
    error = "Amount cannot be negative"

    List B
    2696115
    error = null

    Expected:
    MATCHED
    error_message = "Amount cannot be negative"*/
    @Test
    void shouldKeepListAErrorWhenGeneratingMatchedRecord() {

        insertListA( "2696115",  "1000.00",  "Amount cannot be negative" );
        insertListB( "2696115", "1000.00", null );
        repository.generateMatchedRecords();

        String error = jdbcTemplate.queryForObject(
                """
                SELECT error_message
                FROM reconciliation_matched
                WHERE order_number = ?
                """,
                String.class,
                "2696115"
        );

        assertEquals(  "Amount cannot be negative",   error );
    }
    //Case 3: Match แต่ List B มี error
  /*  A error = null
    B error = "Net total is required"
    Expected:
    error_message = "Net total is required"*/

    @Test
    void shouldKeepListBErrorWhenGeneratingMatchedRecord() {

        insertListA( "2696115", "1000.00",  null  );
        insertListB( "2696115", "1000.00", "Net total is required" );
        repository.generateMatchedRecords();

        String error = jdbcTemplate.queryForObject(
                """
                SELECT error_message
                FROM reconciliation_matched
                WHERE order_number = ?
                """,
                String.class,
                "2696115"
        );

        assertEquals( "Net total is required", error );
    }




    //Case 4: ทั้ง A และ B มี error    test logic sql a.error_message || ';' || b.error_message
    /*A error = "Amount cannot be negative"
    B error = "Net total is required"

    Expected: Amount cannot be negative;Net total is required*/

    @Test
    void shouldCombineListAAndListBErrorsWhenBothHaveErrors() {

        insertListA( "2696115",  "1000.00", "Amount cannot be negative" );

        insertListB(
                "2696115",
                "1000.00",
                "Net total is required"
        );

        repository.generateMatchedRecords();

        String error = jdbcTemplate.queryForObject(
                """
                SELECT error_message
                FROM reconciliation_matched
                WHERE order_number = ?
                """,
                String.class,
                "2696115"
        );

        assertEquals(
                "Amount cannot be negative;Net total is required",
                error
        );
    }
    //Case 5: ไม่มี error ทั้งสองฝั่ง
    /*Expected: error_message = null*/
    @Test
    void shouldHaveNullErrorWhenBothRecordsAreValid() {

        insertListA("2696115", "1000.00", null);
        insertListB("2696115", "1000.00", null);

        repository.generateMatchedRecords();

        String error = jdbcTemplate.queryForObject(
                """
                SELECT error_message
                FROM reconciliation_matched
                WHERE order_number = ?
                """,
                String.class,
                "2696115"
        );

        assertNull(error);
    }

    //Case 6: Missing In A
   /* Scenario:
    A:
            2696115

    B:
            2696115
            2696116

    Expected:
    Missing In A:
            2696116*/
    @Test
    void shouldGenerateMissingInARecord() {

        insertListA("2696115", "1000.00", null);
        insertListB("2696115", "1000.00", null);
        insertListB("2696116", "2000.00", null);

        repository.generateMissingInARecords();

        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM reconciliation_missing_in_a
                WHERE invoice_number = ?
                """,
                Integer.class,
                "2696116"
        );

        assertEquals(1, count);
    }
    //Case 7: Missing In B
   /* Scenario:
    A:
            2696115
            2696116

    B:
            2696115

    Expected:
    Missing In B:
            2696116*/
    @Test
    void shouldGenerateMissingInBRecord() {

        insertListA("2696115", "1000.00", null);
        insertListA("2696116", "2000.00", null);
        insertListB("2696115", "1000.00", null);

        repository.generateMissingInBRecords();

        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM reconciliation_missing_in_b
                WHERE order_number = ?
                """,
                Integer.class,
                "2696116"
        );

        assertEquals(1, count);
    }

    //Case 8: mark Reconciled  A
/* Scenario:
    A:
            2696115
            2696116

    B:
            2696115

    Expected:
    Missing In B:
            A 2696115 → true
            A 2696116 → false
            */

    @Test
    void shouldMarkOnlyMatchingListAAsReconciled() {

        insertListA("2696115", "1000.00", null);
        insertListA("2696116", "2000.00", null);
        insertListB("2696115", "1000.00", null);

        repository.markReconciledListA();

        Boolean matched = jdbcTemplate.queryForObject(
                """
                SELECT reconciled
                FROM reconciliation_list_a
                WHERE order_number = ?
                """,
                Boolean.class,
                "2696115"
        );

        Boolean missing = jdbcTemplate.queryForObject(
                """
                SELECT reconciled
                FROM reconciliation_list_a
                WHERE order_number = ?
                """,
                Boolean.class,
                "2696116"
        );

        assertTrue(matched);
        assertFalse(missing);
    }

    //Case 9: mark Reconciled  B
    @Test
    void shouldMarkOnlyMatchingListBAsReconciled() {

        insertListA("2696115", "1000.00", null);
        insertListB("2696115", "1000.00", null);
        insertListB("2696116", "2000.00", null);

        repository.markReconciledListB();

        Boolean matched = jdbcTemplate.queryForObject(
                """
                SELECT reconciled
                FROM reconciliation_list_b
                WHERE invoice_number = ?
                """,
                Boolean.class,
                "2696115"
        );

        Boolean missing = jdbcTemplate.queryForObject(
                """
                SELECT reconciled
                FROM reconciliation_list_b
                WHERE invoice_number = ?
                """,
                Boolean.class,
                "2696116"
        );

        assertTrue(matched);
        assertFalse(missing);
    }
    //Case 9: clear Previous Data ลบ ทุก output table
    @Test
    void shouldClearPreviousReconciliationData() {

        insertListA("2696115", "1000.00", null);
        insertListB("2696115", "1000.00", null);

        repository.generateMatchedRecords();

        repository.clearPreviousData();

        assertEquals( 0,  count("reconciliation_list_a")  );
        assertEquals(  0,  count("reconciliation_list_b") );
        assertEquals(  0,  count("reconciliation_matched")  );
        assertEquals(   0,   count("reconciliation_missing_in_a") );
        assertEquals(  0,  count("reconciliation_missing_in_b") );
    }

    private int count(String table) {
        Integer count = jdbcTemplate.queryForObject(  "SELECT COUNT(*) FROM " + table,   Integer.class );
        return Objects.requireNonNull(count);
    }
    private void insertListA(  String orderNumber,  String amount,  String errorMessage  ) {

        jdbcTemplate.update(
                """
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
                """,
                "1",
                orderNumber,
                LocalDate.of(2025, 4, 16),
                new BigDecimal(amount),
                new BigDecimal("-10.00"),
                new BigDecimal("-5.00"),
                new BigDecimal("985.00"),
                "SUCCESS",
                errorMessage
        );
    }
    private void insertListB(  String invoiceNumber,  String amount,  String errorMessage  ) {
        jdbcTemplate.update(
                """
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
                """,
                "1",
                invoiceNumber,
                LocalDate.of(2025, 4, 16),
                new BigDecimal(amount),
                new BigDecimal("-10.00"),
                new BigDecimal("-5.00"),
                new BigDecimal("985.00"),
                "4111111111111111",
                "SUCCESS",
                errorMessage
        );
    }


    //not use
    @Disabled
    @Test
    void saveError() {
    }
    @Disabled
    @Test
    void clearErrorRecords() {
    }
    @Disabled
    @Test
    void findAllErrors() {
    }
}