package com.cp.assignment.miniproject.batch.process;

import com.cp.assignment.miniproject.batch.model.ListATransaction;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ListAValidationProcessorTest {
           /* | Case                 | Expected              |
            | -------------------- | --------------------- |
            | item = null          | `ValidationException` |
            | orderNumber ว่าง     | `ValidationException` |
            | transactionDate null | error message         |
            | amount null          | error message         |
            | amount ติดลบ         | error message         |
            | fees1 null           | error message         |
            | fees1 ไม่ติดลบ       | error message         |
            | fees2 null           | error message         |
            | fees2 ไม่ติดลบ       | error message         |
            | netTotal null        | error message         |
            | netTotal ติดลบ       | error message         |
            | status ว่าง          | error message         |
            | valid record         | `errorMessage = ""`   |*/

    private ListAValidationProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new ListAValidationProcessor();
    }

    @Test
    void shouldReturnValidationErrorWhenTransactionDateIsNull() {

        ListATransaction item = createValidListA();
        item.setTransactionDate(null);

        ListATransaction result = processor.process(item);

        assertNotNull(result);
        assertEquals( "Transaction date is required",  result.getErrorMessage()  );
    }

    @Test
    void shouldReturnValidationErrorWhenAmountIsNull() {

        ListATransaction item = createValidListA();
        item.setAmount(null);

        ListATransaction result = processor.process(item);

        assertEquals( "Amount is required", result.getErrorMessage()  );
    }

    @Test
    void shouldReturnValidationErrorWhenAmountIsNegative() {

        ListATransaction item = createValidListA();
        item.setAmount(new BigDecimal("-100.00"));

        ListATransaction result = processor.process(item);

        assertEquals( "Amount cannot be negative",  result.getErrorMessage() );
    }

    @Test
    void shouldReturnValidationErrorWhenFees1IsNull() {

        ListATransaction item = createValidListA();
        item.setFees1(null);

        ListATransaction result = processor.process(item);

        assertEquals( "Fees1 is required",  result.getErrorMessage() );
    }

    @Test
    void shouldReturnValidationErrorWhenFees1IsNotNegative() {

        ListATransaction item = createValidListA();
        item.setFees1(new BigDecimal("10.00"));

        ListATransaction result = processor.process(item);

        assertEquals( "Fees1 should be negative", result.getErrorMessage() );
    }

    @Test
    void shouldReturnValidationErrorWhenFees2IsNull() {

        ListATransaction item = createValidListA();
        item.setFees2(null);

        ListATransaction result = processor.process(item);

        assertEquals( "Fees2 is required", result.getErrorMessage() );
    }

    @Test
    void shouldReturnValidationErrorWhenFees2IsNotNegative() {

        ListATransaction item = createValidListA();
        item.setFees2(new BigDecimal("10.00"));

        ListATransaction result = processor.process(item);

        assertEquals(  "Fees2 should be negative", result.getErrorMessage()  );
    }

    @Test
    void shouldReturnValidationErrorWhenNetTotalIsNull() {

        ListATransaction item = createValidListA();
        item.setNetTotal(null);

        ListATransaction result = processor.process(item);

        assertEquals( "Net total is required",  result.getErrorMessage() );
    }

    @Test
    void shouldReturnValidationErrorWhenNetTotalIsNegative() {

        ListATransaction item = createValidListA();
        item.setNetTotal(new BigDecimal("-100.00"));

        ListATransaction result = processor.process(item);

        assertEquals(  "Net total cannot be negative",  result.getErrorMessage() );
    }

    @Test
    void shouldReturnValidationErrorWhenStatusIsBlank() {

        ListATransaction item = createValidListA();
        item.setStatus("");

        ListATransaction result = processor.process(item);

        assertEquals( "Status is required",  result.getErrorMessage() );
    }

    @Test
    void shouldReturnEmptyErrorMessageWhenListAIsValid() {

        ListATransaction item = createValidListA();

        ListATransaction result = processor.process(item);

        assertNotNull(result);
        assertEquals("", result.getErrorMessage());
    }

    @Test
    void shouldThrowValidationExceptionWhenListAIsNull() {
        assertThrows(  ValidationException.class, () -> processor.process(null) );
    }

    @Test
    void shouldThrowValidationExceptionWhenOrderNumberIsBlank() {

        ListATransaction item = createValidListA();
        item.setOrderNumber("");

        jakarta.validation.ValidationException exception =  assertThrows(    ValidationException.class,  () -> processor.process(item) );

        assertEquals(   "Order number is required",  exception.getMessage() );
    }

    private ListATransaction createValidListA() {

        return ListATransaction.builder()
                .rowNumber("1")
                .orderNumber("2696115")
                .transactionDate(LocalDate.of(2025, 4, 16))
                .amount(new BigDecimal("1000.00"))
                .fees1(new BigDecimal("-10.00"))
                .fees2(new BigDecimal("-5.00"))
                .netTotal(new BigDecimal("985.00"))
                .status("SUCCESS")
                .build();
    }
}