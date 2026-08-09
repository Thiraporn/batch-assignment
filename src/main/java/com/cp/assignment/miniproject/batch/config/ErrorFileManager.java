package com.cp.assignment.miniproject.batch.config;


import com.cp.assignment.miniproject.batch.model.ErrorRecord;
import com.cp.assignment.miniproject.common.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

//เพราะ ErrorWriter ไม่ควรมีหน้าที่ลบไฟล์
@Component
@Slf4j
public class ErrorFileManager {
    private final Path errorFile;

    public ErrorFileManager(@Value("${batch.output.error}") String filePath) {
        this.errorFile = Path.of(filePath);
    }
    //delete file when start
    public void deletePreviousErrorFile() throws IOException {
        boolean deleted = Files.deleteIfExists(errorFile);
        if (deleted) {
            log.info("Deleted previous error file: {}", errorFile);
        } else {
            log.info("No previous error file found: {}", errorFile
            );
        }
    }

    //empty file with header
    public void createEmptyErrorFile() throws IOException {
        Path parent = errorFile.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        Files.writeString(  errorFile, "sourceFile,rowNumber,reference,errorMessage" + System.lineSeparator()  );
    }

    public synchronized void appendError( ErrorRecord error  ) throws IOException {

        String line =   String.join(
                        ",",
                        CommonUtils.csv(error.getSourceFile()),
                        CommonUtils.csv(error.getRowNumber()),
                        CommonUtils.csv(error.getReference()),
                        CommonUtils.csv(error.getErrorMessage())
                );

        Files.writeString(
                errorFile,
                line + System.lineSeparator(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
    }

}