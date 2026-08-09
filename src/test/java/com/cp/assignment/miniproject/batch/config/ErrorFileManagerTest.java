package com.cp.assignment.miniproject.batch.config;


import com.cp.assignment.miniproject.batch.model.ErrorRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ErrorFileManagerTest {

    @TempDir
    Path tempDir;

    private Path errorFile;
    private ErrorFileManager errorFileManager;

    @BeforeEach
    void setUp() {
        errorFile = tempDir.resolve("Error_Records.csv");
        errorFileManager = new ErrorFileManager(errorFile.toString());
    }

    @Test
    void shouldCreateEmptyErrorFileWithHeader() throws IOException {

        errorFileManager.createEmptyErrorFile();
        assertTrue(Files.exists(errorFile));

        List<String> lines =  Files.readAllLines(errorFile);

        assertEquals(1, lines.size());
        assertEquals(  "sourceFile,rowNumber,reference,errorMessage",  lines.get(0)  );
    }

    @Test
    void shouldDeleteExistingErrorFile() throws IOException {

        Files.writeString(  errorFile, "old data" );
        assertTrue(Files.exists(errorFile));
        errorFileManager.deletePreviousErrorFile();
        assertFalse(Files.exists(errorFile));
    }

    @Test
    void shouldNotFailWhenDeletingNonExistingFile()   throws IOException {

        assertFalse(Files.exists(errorFile));
        assertDoesNotThrow(   () -> errorFileManager.deletePreviousErrorFile()  );
        assertFalse(Files.exists(errorFile));
    }

    @Test
    void shouldAppendErrorRecord()   throws IOException {

        errorFileManager.createEmptyErrorFile();

        ErrorRecord error = ErrorRecord.builder()
                .sourceFile("ListA.csv")
                .rowNumber("10")
                .reference("2696115")
                .errorMessage("Order number is required")
                .build();

        errorFileManager.appendError(error);

        List<String> lines = Files.readAllLines(errorFile);

        assertEquals(2, lines.size());
        assertEquals( "sourceFile,rowNumber,reference,errorMessage", lines.get(0)  );
        assertEquals("ListA.csv,10,2696115,Order number is required",  lines.get(1) );
    }

    @Test
    void shouldAppendMultipleErrors()   throws IOException {

        errorFileManager.createEmptyErrorFile();

        ErrorRecord error1 = ErrorRecord.builder()
                .sourceFile("ListA.csv")
                .rowNumber("10")
                .reference("2696115")
                .errorMessage("Order number is required")
                .build();

        ErrorRecord error2 = ErrorRecord.builder()
                .sourceFile("ListB.csv")
                .rowNumber("20")
                .reference("2696116")
                .errorMessage("Invoice number is required")
                .build();

        errorFileManager.appendError(error1);
        errorFileManager.appendError(error2);

        List<String> lines =  Files.readAllLines(errorFile);

        assertEquals(3, lines.size());
        assertEquals(  "ListA.csv,10,2696115,Order number is required",  lines.get(1) );
        assertEquals(  "ListB.csv,20,2696116,Invoice number is required",  lines.get(2)  );
    }

    @Test
    void shouldCreateParentDirectoryWhenItDoesNotExist()
            throws IOException {

        Path nestedFile = tempDir
                .resolve("output")
                .resolve("error")
                .resolve("Error_Records.csv");

        ErrorFileManager manager =  new ErrorFileManager(nestedFile.toString());
        manager.createEmptyErrorFile();
        assertTrue(Files.exists(nestedFile));
    }
}