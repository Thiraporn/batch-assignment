package com.cp.assignment.miniproject.batch.writer;

import com.cp.assignment.miniproject.batch.model.ReconciliationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemStreamWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReconciliationWriter
        implements ItemStreamWriter<ReconciliationResult> {

    private final MatchedCsvWriter matchedWriter;

    private final MissingInACsvWriter missingInAWriter;

    private final ErrorWriter errorWriter;

    @Override
    public void write(  Chunk<? extends ReconciliationResult> chunk  ) throws Exception {

        for (ReconciliationResult result : chunk) {

            if (result.getStatus() == null) {
                continue;
            }

            switch (result.getStatus()) {

                case MATCHED:
                    matchedWriter.write(new Chunk<>(List.of(result)));
                    break;

                case MISSING_IN_A:
                    missingInAWriter.write(new Chunk<>(List.of(result)));
                    break;

                case ERROR:
                    //not use currently/need more requirements
                    errorWriter.write(new Chunk<>(List.of(result)));
                    // errorWriter.write(result);
                    break;

                case MISSING_IN_B:
                    // MISSING_IN_B is handled
                    // in the final step.
                    break;
            }
        }
    }
}