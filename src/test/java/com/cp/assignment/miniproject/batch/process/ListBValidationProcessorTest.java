package com.cp.assignment.miniproject.batch.process;

import com.cp.assignment.miniproject.batch.model.ListBTransaction;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ListBValidationProcessorTest {
            /*| Case                 | Expected              |
            | -------------------- | --------------------- |
            | item = null          | `ValidationException` |
            | invoiceNumber ว่าง   | `ValidationException` |
            | transactionDate null | error                 |
            | amount null          | error                 |
            | amount ติดลบ         | error                 |
            | fees1 null           | error                 |
            | fees1 ไม่ติดลบ       | error                 |
            | fees2 null           | error                 |
            | fees2 ไม่ติดลบ       | error                 |
            | netTotal null        | error                 |
            | netTotal ติดลบ       | error                 |
            | cardNumber ว่าง      | error                 |
            | status ว่าง          | error                 |
            | valid record         | ไม่มี error           |
*/
    private ListBValidationProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new ListBValidationProcessor();
    }

    @Test
    void shouldThrowValidationExceptionWhenListBIsNull() {
        assertThrows(  ValidationException.class,    () -> processor.process(null) );
    }

    @Test
    void shouldThrowValidationExceptionWhenInvoiceNumberIsBlank() {

        ListBTransaction item = createValidListB();
        item.setInvoiceNumber("");

        ValidationException exception =  assertThrows(  ValidationException.class,  () -> processor.process(item)  );

        assertEquals(  "Invoice number is required",  exception.getMessage() );
    }

    @Test
    void shouldReturnValidationErrorWhenTransactionDateIsNull() {

        ListBTransaction item = createValidListB();
        item.setTransactionDate(null);

        ListBTransaction result = processor.process(item);

        assertEquals( "Transaction date is required", result.getErrorMessage()  );
    }

    @Test
    void shouldReturnValidationErrorWhenAmountIsNull() {

        ListBTransaction item = createValidListB();
        item.setAmount(null);

        ListBTransaction result = processor.process(item);

        assertEquals(  "Amount is required",  result.getErrorMessage() );
    }

    @Test
    void shouldReturnValidationErrorWhenAmountIsNegative() {

        ListBTransaction item = createValidListB();
        item.setAmount(new BigDecimal("-100.00"));

        ListBTransaction result = processor.process(item);

        assertEquals( "Amount cannot be negative",  result.getErrorMessage() );
    }

    @Test
    void shouldReturnValidationErrorWhenFees1IsNull() {

        ListBTransaction item = createValidListB();
        item.setFees1(null);

        ListBTransaction result = processor.process(item);

        assertEquals( "Fees1 is required",  result.getErrorMessage()  );
    }

    @Test
    void shouldReturnValidationErrorWhenFees1IsNotNegative() {

        ListBTransaction item = createValidListB();
        item.setFees1(new BigDecimal("10.00"));

        ListBTransaction result = processor.process(item);

        assertEquals( "Fees1 should be negative",    result.getErrorMessage()  );
    }

    @Test
    void shouldReturnValidationErrorWhenFees2IsNull() {

        ListBTransaction item = createValidListB();
        item.setFees2(null);

        ListBTransaction result = processor.process(item);

        assertEquals( "Fees2 is required",  result.getErrorMessage()  );
    }

    @Test
    void shouldReturnValidationErrorWhenFees2IsNotNegative() {

        ListBTransaction item = createValidListB();
        item.setFees2(new BigDecimal("10.00"));

        ListBTransaction result = processor.process(item);

        assertEquals( "Fees2 should be negative",   result.getErrorMessage() );
    }

    @Test
    void shouldReturnValidationErrorWhenNetTotalIsNull() {

        ListBTransaction item = createValidListB();
        item.setNetTotal(null);

        ListBTransaction result = processor.process(item);

        assertEquals( "Net total is required",   result.getErrorMessage()  );
    }

    @Test
    void shouldReturnValidationErrorWhenNetTotalIsNegative() {

        ListBTransaction item = createValidListB();
        item.setNetTotal(new BigDecimal("-100.00"));

        ListBTransaction result = processor.process(item);

        assertEquals( "Net total cannot be negative",  result.getErrorMessage()  );
    }

    @Test
    void shouldReturnValidationErrorWhenCardNumberIsBlank() {

        ListBTransaction item = createValidListB();
        item.setCardNumber("");

        ListBTransaction result = processor.process(item);

        assertEquals( "Card number is required", result.getErrorMessage() );
    }

    @Test
    void shouldReturnValidationErrorWhenStatusIsBlank() {

        ListBTransaction item = createValidListB();
        item.setStatus("");

        ListBTransaction result = processor.process(item);

        assertEquals(  "Status is required",  result.getErrorMessage() );
    }

    @Test
    void shouldReturnEmptyErrorMessageWhenListBIsValid() {

        ListBTransaction item = createValidListB();

        ListBTransaction result = processor.process(item);

        assertNotNull(result);
        assertEquals("", result.getErrorMessage());
    }

    private ListBTransaction createValidListB() {

        return ListBTransaction.builder()
                .rowNumber("7")
                .invoiceNumber("2696115")
                .transactionDate(LocalDate.of(2025, 4, 16))
                .amount(new BigDecimal("1000.00"))
                .fees1(new BigDecimal("-10.00"))
                .fees2(new BigDecimal("-5.00"))
                .netTotal(new BigDecimal("985.00"))
                .cardNumber("4111111111111111")
                .status("SUCCESS")
                .build();
    }
}

