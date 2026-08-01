package com.cp.assignment.miniproject.batch.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListBTransaction {
    //#|Invoice Number|Transaction Date|Amount|Fees1|Fees2|Net Total|Card Number|Status
    private String rowNumber;
    private String invoiceNumber;// merchant order number
    private LocalDate transactionDate;
    private BigDecimal amount;
    private BigDecimal fees1;
    private BigDecimal fees2;
    private BigDecimal netTotal;
    private String cardNumber;
    private String status;


}