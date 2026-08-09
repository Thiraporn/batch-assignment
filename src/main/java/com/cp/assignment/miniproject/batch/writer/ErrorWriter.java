package com.cp.assignment.miniproject.batch.writer;

import com.cp.assignment.miniproject.batch.config.ErrorFileManager;
import com.cp.assignment.miniproject.batch.model.ErrorRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class ErrorWriter {
    private final ErrorFileManager errorFileManager;

    public void writeError(String sourceFile, String rowNumber, String reference, String errorMessage) throws IOException {
        ErrorRecord errorRecord =
                ErrorRecord.builder()
                .sourceFile(sourceFile)
                .rowNumber(rowNumber)
                .reference(reference)
                .errorMessage(errorMessage)
                .build();

        errorFileManager.appendError(errorRecord);
    }
}