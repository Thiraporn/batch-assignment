package com.cp.assignment.miniproject.batch.writer;

import com.cp.assignment.miniproject.batch.model.ReconciliationResult;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.batch.infrastructure.item.ItemStreamWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileItemWriter;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ErrorWriter implements ItemStreamWriter<ReconciliationResult> {

    private final FlatFileItemWriter<ReconciliationResult> writer;

    public ErrorWriter(  @Value("${batch.output.error}") String filePath  ) {

        this.writer = new FlatFileItemWriterBuilder<ReconciliationResult>()
                .name("errorWriter")
                .resource(new org.springframework.core.io.FileSystemResource(filePath))
                .headerCallback(w -> w.write( "invoiceNumber,errorMessage" )
                )
                .lineAggregator(result -> {
                    String invoiceNumber = "";
                    if (result.getListB() != null) {
                        invoiceNumber = result.getListB().getInvoiceNumber();
                    }

                    String errorMessage =  result.getErrorMessage() == null  ? ""   : result.getErrorMessage();

                    return String.join(
                            ",",
                            invoiceNumber,
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