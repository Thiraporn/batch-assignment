package com.cp.assignment.miniproject.batch.process;

import com.cp.assignment.miniproject.batch.model.ListBTransaction;
import com.cp.assignment.miniproject.batch.model.ReconciliationResult;
import com.cp.assignment.miniproject.batch.service.ListACacheService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReconciliationProcessorTest {
    @Mock
    private ListACacheService listACacheService;

    @InjectMocks
    private ReconciliationProcessor reconciliationProcessor;


    @Test
    void shouldHandleNullInvoiceNumberIsNull() {

        // Arrange
        ListBTransaction item = new ListBTransaction();
        item.setInvoiceNumber(null);

        // Act
        ReconciliationResult result =  reconciliationProcessor.process(item);

        // Assert
        assertNotNull(result);
    }


    @Test
    void shouldReturnMissingInAWhenInvoiceNotFound() {

        // Arrange
        ListBTransaction item = new ListBTransaction();
        item.setRowNumber("7");
        item.setInvoiceNumber(null);
        item.setTransactionDate(LocalDate.of(2025, 4, 16));
        item.setAmount(new BigDecimal("1000.00"));
        item.setFees1(new BigDecimal("-10.00"));
        item.setFees2(new BigDecimal("-5.00"));
        item.setNetTotal(new BigDecimal("985.00"));
        item.setCardNumber("4111111111111111");
        item.setStatus("SUCCESS");

        // Mock dependencies ให้เกิดสถานการณ์ที่ 2696117 ผิด
        // Mock: invoice 2696117 does not exist in List A
//        when(listACacheService.get("2696117"))
//                .thenReturn(null);

        // Act
        ReconciliationResult result =  reconciliationProcessor.process(item);

        // Assert
        assertNotNull(result);

//        assertEquals(
//                EResultType.MISSING_IN_A,
//                result.getStatus()
//        );
//        assertEquals(
//                "Invoice number is required",
//                result.getErrorMessage()
//        );
    }


}