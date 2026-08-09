package com.cp.assignment.miniproject.batch.config;

import com.cp.assignment.miniproject.batch.model.ErrorRecord;
import com.cp.assignment.miniproject.batch.model.ReconciliationRecord;
import com.cp.assignment.miniproject.batch.writer.ErrorWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReconciliationSkipListener  implements SkipListener<ReconciliationRecord, ReconciliationRecord> {

    private final ErrorWriter errorWriter;

    @Override
    public void onSkipInRead(Throwable t) {

       writeError(  "UNKNOWN", "UNKNOWN", "UNKNOWN",    getErrorMessage(t));
    }

    @Override
    public void onSkipInProcess(  ReconciliationRecord item,  Throwable t
    ) {

        writeError(   item.getSourceFile(),  item.getRowNumber(),   item.getReference() ,  getErrorMessage(t) );
    }
    // ============================================================
    // SKIP DURING WRITE
    // ============================================================

    @Override
    public void onSkipInWrite(ReconciliationRecord item, Throwable t
    ) {
        writeError(   item.getSourceFile(),  item.getRowNumber(),   item.getReference(),   getErrorMessage(t) );
    }
    // ============================================================
    // WRITE ERROR
    // ============================================================
    private void writeError(
            String sourceFile,
            String rowNumber,
            String reference,
            String errorMessage
    ) {

        try {
            errorWriter.writeError(
                    sourceFile,
                    rowNumber,
                    reference,
                    errorMessage
            );

        } catch (Exception e) {

            /*
             * Error Report เป็นส่วนหนึ่งของ Batch result
             *
             * ถ้าเขียน Error_Records.csv ไม่ได้
             * ไม่ควรกลบปัญหาแล้วปล่อย Batch ทำงานต่อ
             */
            log.error(  "Failed to write error report",    e    );
            throw new RuntimeException(
                    "Failed to write Error_Records.csv",
                    e
            );
        }
    }

    // ============================================================
    // ERROR MESSAGE
    // ============================================================
    private String getErrorMessage(Throwable t) {

        if (t == null) {
            return "Unknown error";
        }

        return t.getMessage() != null
                ? t.getMessage()
                : t.getClass().getSimpleName();
    }

}