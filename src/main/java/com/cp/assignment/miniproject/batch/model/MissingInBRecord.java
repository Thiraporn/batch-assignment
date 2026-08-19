package com.cp.assignment.miniproject.batch.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class MissingInBRecord {
    private String orderNumber;
    private LocalDate transactionDate;
    private BigDecimal amount;
    private String errorMessage;
}