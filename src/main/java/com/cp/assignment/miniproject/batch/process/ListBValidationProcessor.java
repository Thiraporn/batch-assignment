package com.cp.assignment.miniproject.batch.process;

import com.cp.assignment.miniproject.batch.model.ListBTransaction;
import com.cp.assignment.miniproject.common.CommonUtils;
import jakarta.validation.ValidationException;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class ListBValidationProcessor   implements ItemProcessor<ListBTransaction, ListBTransaction> {

    @Override
    public ListBTransaction process(ListBTransaction item) {

        // 1. Validate
        String validationError = validate(item);
        item.setErrorMessage(validationError);

        // 2. Business processing

        return item;
    }

    private String validate(ListBTransaction item) {
        // Errors that must go to Error_Records.csv
        if (item == null) {
            throw new ValidationException(  "List B transaction is null" );
        }
        // Errors that must go to Error_Records.csv
        if (CommonUtils.isBlank(item.getInvoiceNumber())) {
            throw new ValidationException(
                    "Invoice number is required"
            );
        }
        // Other validation errors
        // remain in the record and will be handled by reconciliation
        if (item.getTransactionDate() == null) {
            return  "Transaction date is required";
        }

        if (item.getAmount() == null) {
            return   "Amount is required";
        }

        if (CommonUtils.isNegative(item.getAmount())) {
            return  "Amount cannot be negative";
        }

        if (item.getFees1() == null) {
            return "Fees1 is required";
        }

        if (!CommonUtils.isNegative(item.getFees1())) {
            return "Fees1 should be negative";
        }

        if (item.getFees2() == null) {
            return "Fees2 is required";
        }

        if (!CommonUtils.isNegative(item.getFees2())) {
            return "Fees2 should be negative";
        }

        if (item.getNetTotal() == null) {
            return "Net total is required";
        }

        if (CommonUtils.isNegative(item.getNetTotal())) {
            return "Net total cannot be negative";
        }

        if (CommonUtils.isBlank(item.getCardNumber())) {
            return "Card number is required";
        }

        if (CommonUtils.isBlank(item.getStatus())) {
            return "Status is required";
        }
        return "";
    }
}