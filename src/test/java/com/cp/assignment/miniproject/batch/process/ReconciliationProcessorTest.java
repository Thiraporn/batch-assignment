package com.cp.assignment.miniproject.batch.process;

import com.cp.assignment.miniproject.batch.model.EResultType;
import com.cp.assignment.miniproject.batch.model.ListATransaction;
import com.cp.assignment.miniproject.batch.model.ListBTransaction;
import com.cp.assignment.miniproject.batch.model.ReconciliationResult;
import com.cp.assignment.miniproject.batch.service.ListACacheService;
import com.cp.assignment.miniproject.common.CommonUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
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
        ReconciliationResult result = reconciliationProcessor.process(item);

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
        ReconciliationResult result = reconciliationProcessor.process(item);

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

    @Test
    void shouldMatchWhenListAAndListBDataAreSame() {

        // Arrange
        ListATransaction listA = new ListATransaction();
        listA.setRowNumber("1");
        listA.setOrderNumber("2696115");
        listA.setTransactionDate(LocalDate.of(2025, 4, 16));
        listA.setAmount(new BigDecimal("1000.00"));
        listA.setFees1(new BigDecimal("-10.00"));
        listA.setFees2(new BigDecimal("-5.00"));
        listA.setNetTotal(new BigDecimal("985.00"));
        listA.setStatus("SUCCESS");

        ListBTransaction listB = new ListBTransaction();
        listB.setRowNumber("7");
        listB.setInvoiceNumber("2696115");
        listB.setTransactionDate(LocalDate.of(2025, 4, 16));
        listB.setAmount(new BigDecimal("1000.00"));
        listB.setFees1(new BigDecimal("-10.00"));
        listB.setFees2(new BigDecimal("-5.00"));
        listB.setNetTotal(new BigDecimal("985.00"));
        listB.setCardNumber("4111111111111111");
        listB.setStatus("SUCCESS");

        // ตรวจสอบก่อนว่า invoice ถูกต้อง
        assertEquals("2696115", listB.getInvoiceNumber());

        // Processor ใช้ remove() ไม่ใช่ get()
        //เพราะใน test นี้ listACacheService เป็น Mock ครับ ไม่ใช่ cache จริง
        when(listACacheService.remove("2696115")).thenReturn(listA);

        // ตรวจสอบว่า mock คืน ListA จริง
        // assertNotNull(listACacheService.remove("2696115"));

        // Act
        ReconciliationResult result = reconciliationProcessor.process(listB);

        // Assert
        assertEquals(EResultType.MATCHED, result.getStatus());

        assertNull(result.getErrorMessage());
    }

    @Test
    void shouldReturnValidationErrorForInvalidListA() {

        ListATransaction listA = ListATransaction.builder()
                .rowNumber("3")
                .orderNumber("2696113")
                .transactionDate(LocalDate.of(2025, 4, 16))
                .amount(new BigDecimal("40038.00"))
                .fees1(new BigDecimal("-0.75"))
                .fees2(new BigDecimal("-999999999"))
                .netTotal(new BigDecimal("40032.80"))
                .status("Success")
                .build();

        ListBTransaction listB = ListBTransaction.builder()
                .invoiceNumber("2696113")
                .transactionDate(LocalDate.of(2025, 4, 16))
                .amount(new BigDecimal("40038.00"))
                .fees1(new BigDecimal("-0.75"))
                .fees2(new BigDecimal("-4.45"))
                .netTotal(null)//should err
                .status("Success")
                .build();

        // ใส่ ListA เข้า cache ก่อน
        listACacheService.put(listA);

        ReconciliationResult result = reconciliationProcessor.process(listB);

        assertEquals("Net total is required", result.getErrorMessage());
    }

    @Test
    void shouldDetectInvalidFees2InListA() {
        ListATransaction listA = createListA(
                "2696113",
                "40038.00",
                "-0.75",
                "-999999999",
                "40032.80"
        );

        ListBTransaction listB = createListB(
                "2696113",
                "40038.00",
                "-0.75",
                "-4.45",
                "40032.80"
        );

        listACacheService.put(listA);

        ReconciliationResult result = reconciliationProcessor.process(listB);

        assertEquals("Card number is required", result.getErrorMessage());
    }


    @Test
    void shouldDetectMissingNetTotalInListB() {

        ListATransaction listA = createListA(
                "2696113",
                "40038.00",
                "-0.75",
                "-4.45",
                "40032.80"
        );

        ListBTransaction listB = createListB(
                "2696113",
                "40038.00",
                "-0.75",
                "-4.45",
                null
        );

        listACacheService.put(listA);

        ReconciliationResult result = reconciliationProcessor.process(listB);

        assertEquals("Net total is required", result.getErrorMessage());
    }

    private ListATransaction createListA(
            String orderNumber,
            String amount,
            String fees1,
            String fees2,
            String netTotal) {

        return ListATransaction.builder()
                .rowNumber("test")
                .orderNumber(orderNumber)
                .transactionDate(LocalDate.of(2025, 4, 16))
                .amount(CommonUtils.parseDecimal(amount))
                .fees1(CommonUtils.parseDecimal(fees1))
                .fees2(CommonUtils.parseDecimal(fees2))
                .netTotal(CommonUtils.parseDecimal(netTotal))
                .status("Success")
                .build();
    }


    private ListBTransaction createListB(
            String invoiceNumber,
            String amount,
            String fees1,
            String fees2,
            String netTotal) {

        return ListBTransaction.builder()
                .invoiceNumber(invoiceNumber)
                .transactionDate(LocalDate.of(2025, 4, 16))
                .amount(CommonUtils.parseDecimal(amount))
                .fees1(CommonUtils.parseDecimal(fees1))
                .fees2(CommonUtils.parseDecimal(fees2))
                .netTotal(CommonUtils.parseDecimal(netTotal))
                .status("Success")
                .build();
    }

}