package com.cp.assignment.miniproject.batch.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorRecord {

    private String sourceFile;
    private String rowNumber;
    private String reference;
    private String errorMessage;
}