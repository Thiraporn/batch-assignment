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
public class ListATransaction {

    //#|Order Number|Transaction Date|Amount|Fees1|Fees2|Net Total|Status
    private String rowNumber;
    private String orderNumber;// merchant order number
    private LocalDate transactionDate;
    private BigDecimal amount;
    private BigDecimal fees1;
    private BigDecimal fees2;
    private BigDecimal netTotal;

    private String status;


}
