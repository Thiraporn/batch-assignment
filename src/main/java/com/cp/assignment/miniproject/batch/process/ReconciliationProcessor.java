package com.cp.assignment.miniproject.batch.process;

import com.cp.assignment.miniproject.batch.model.EResultType;
import com.cp.assignment.miniproject.batch.model.ListATransaction;
import com.cp.assignment.miniproject.batch.model.ListBTransaction;
import com.cp.assignment.miniproject.batch.model.ReconciliationResult;
import com.cp.assignment.miniproject.batch.service.ListACacheService;
import com.cp.assignment.miniproject.common.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class ReconciliationProcessor implements ItemProcessor<ListBTransaction, ReconciliationResult> {

    private final ListACacheService cacheService;
    //process matching
    @Override
    public ReconciliationResult process(ListBTransaction listB) {


        // Validate List B
        String validationBError = validateListB(listB);

        String invoiceNumber = listB.getInvoiceNumber();

        /*
         * O(1) lookup
         *
         * If found:
         * remove from cache immediately.
         *
         * Therefore, remaining records in cache
         * after List B finishes are MISSING_IN_B.
         */

        ListATransaction listA = cacheService.remove(invoiceNumber);

        //ListATransaction listA = cacheService.get(invoiceNumber);

        // List B exists but List A does not
        if (listA == null) {
            return ReconciliationResult.builder()
                    .status(EResultType.MISSING_IN_A)
                    .listB(listB)
                    .errorMessage(validationBError)
                    .build();
        }

        // Validate List A
        String validationAError = validateListA(listA);

        // Found in both A and B
        cacheService.remove(invoiceNumber);
        return ReconciliationResult.builder()
                .status(EResultType.MATCHED)
                .listA(listA)
                .listB(listB)
                .errorMessage(validationAError)
                .build();
    }
    // Validate List A in final step
    public ReconciliationResult processMissingInB(ListATransaction listA) {
        String validationAError = validateListA(listA);

        return ReconciliationResult.builder()
                .status(EResultType.MISSING_IN_B)
                .listA(listA)
                .errorMessage(validationAError)
                .build();

    }

    private String validateListB(ListBTransaction listB) {

            if (listB == null) {
                return "List B transaction is null";
            }

            if (CommonUtils.isBlank(listB.getInvoiceNumber())) {
                return "Invoice number is required";
            }

            if (listB.getTransactionDate() == null) {
                return "Transaction date is required";
            }

            if (listB.getAmount() == null) {
                return "Amount is required";
            }

            if (listB.getFees1() == null) {
                return "Fees1 is required";
            }

            if (!CommonUtils.isNegative(listB.getFees1())) {
                return "Fees1 should be negative";
            }

            if (listB.getFees2() == null) {
                return "Fees2 is required";
            }
            if (!CommonUtils.isNegative(listB.getFees2())) {
                return "Fees2 should be negative";
            }

            if (CommonUtils.isNegative(listB.getAmount())) {
                return "Amount cannot be negative";
            }

            if (listB.getNetTotal() == null) {
                return "Net total is required";
            }

            if (CommonUtils.isNegative(listB.getNetTotal())) {
                return "Net total cannot be negative";
            }

            if (CommonUtils.isBlank(listB.getCardNumber())) {
                return "Card number is required";
            }

            if (CommonUtils.isBlank(listB.getStatus())) {
                return "Status is required";
            }

            return null;

    }
    private String validateListA(ListATransaction listA) {
        if (CommonUtils.isBlank(listA.getOrderNumber())) {
            return "Order number is required";
        }

        if (listA.getTransactionDate() == null) {
            return "Transaction date is required";
        }

        if (listA.getAmount() == null) {
            return "Amount is required";
        }

        if (CommonUtils.isNegative(listA.getAmount())) {
            return "Amount cannot be negative";
        }
        if (listA.getFees1() == null) {
            return "Fees1 is required";
        }

        if (!CommonUtils.isNegative(listA.getFees1())) {
            return "Fees1 should be negative";
        }

        if (listA.getFees2() == null) {
            return "Fees2 is required";
        }
        if (!CommonUtils.isNegative(listA.getFees2())) {
            return "Fees2 should be negative";
        }

        if (listA.getNetTotal() == null) {
            return "Net total is required";
        }

        if (CommonUtils.isNegative(listA.getNetTotal())) {
            return "Net total cannot be negative";
        }

        if (CommonUtils.isBlank(listA.getStatus())) {
            return "Status is required";
        }

        return null;
    }


    }