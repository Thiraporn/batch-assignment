package com.cp.assignment.miniproject.batch.model;



import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ReconciliationResult {
    private EResultType status;
    private ListATransaction listA;
    private ListBTransaction listB;
    private String errorMessage;

}