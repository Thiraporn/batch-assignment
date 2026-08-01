package com.cp.assignment.miniproject.batch.writer;

import com.cp.assignment.miniproject.batch.model.ReconciliationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.batch.infrastructure.item.ItemStreamWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileItemWriter;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MatchedCsvWriter implements ItemStreamWriter<ReconciliationResult> {

    private final FlatFileItemWriter<ReconciliationResult> writer;

    public MatchedCsvWriter(@Value("${batch.output.matched}") String filePath) {
        // log.debug("Processing invoice: {}", item.getInvoiceNumber());
        this.writer = new FlatFileItemWriterBuilder<ReconciliationResult>()
                .name("matchedWriter")
                .resource(new org.springframework.core.io.FileSystemResource(filePath))
                .headerCallback(w -> w.write("orderNumber,invoiceNumber,listAAmount,listBAmount,errorMessage")
                )
                .lineAggregator(result -> {
                    String errorMessage = result.getErrorMessage() == null ? "" : result.getErrorMessage();
                    return String.join(
                            ",",
                            result.getListA().getOrderNumber(),
                            result.getListB().getInvoiceNumber(),
                            result.getListA().getAmount().toPlainString(),
                            result.getListB().getAmount().toPlainString(),
                            errorMessage

                    );
                })
                .build();
    }

    @Override
    public void open(ExecutionContext executionContext) {
        writer.open(executionContext);
    }

    @Override
    public void update(ExecutionContext executionContext) {
        writer.update(executionContext);
    }

    @Override
    public void close() {
        writer.close();
    }

    @Override
    public void write(Chunk<? extends ReconciliationResult> chunk) throws Exception {
        writer.write(chunk);
    }

}