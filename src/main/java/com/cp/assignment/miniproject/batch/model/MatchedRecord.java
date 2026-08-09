package com.cp.assignment.miniproject.batch.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class MatchedRecord {

    private String orderNumber;
    private String invoiceNumber;
    private BigDecimal listAAmount;
    private BigDecimal listBAmount;
    private String errorMessage;
}
