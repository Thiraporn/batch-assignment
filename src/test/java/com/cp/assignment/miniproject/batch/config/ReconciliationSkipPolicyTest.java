package com.cp.assignment.miniproject.batch.config;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

class ReconciliationSkipPolicyTest {
    private ReconciliationSkipPolicy skipPolicy;

    @BeforeEach
    void setUp() {
        skipPolicy = new ReconciliationSkipPolicy();
    }

    @Test
    void shouldSkipValidationException() { //ValidationException       → true    Expected :   SKIP
        boolean result = skipPolicy.shouldSkip(
                new ValidationException("Order number is required"),
                //new ValidationException("Invalid data"),
                0
        );

        assertTrue(result);
    }

    @Test
    void shouldSkipDateTimeParseException() {// DateTimeParseException    → true  Expected :   SKIP
        boolean result = skipPolicy.shouldSkip(
                new DateTimeParseException(   "Invalid date",  "2025-99-99",   0  ), 0 );
        assertTrue(result);
    }

    @Test
    void shouldSkipNumberFormatException() {//  NumberFormatException     → true  Expected :   SKIP
        boolean result = skipPolicy.shouldSkip(  new NumberFormatException("Invalid number"), 0 );
        assertTrue(result);
    }

    @Test
    void shouldNotSkipSQLException() {// SQLException              → false   Expected : DO NOT SKIP
        boolean result = skipPolicy.shouldSkip(   new SQLException("Database error"), 0  );
        assertFalse(result);
    }

    @Test
    void shouldNotSkipIOException() {//IOException               → false   Expected : DO NOT SKIP
        boolean result = skipPolicy.shouldSkip( new IOException("File error"), 0 );
        assertFalse(result);
    }

    @Test
    void shouldNotSkipNullPointerException() {//NullPointerException      → false   Expected : DO NOT SKIP
        boolean result = skipPolicy.shouldSkip(  new NullPointerException("Programming error"), 0  );
        assertFalse(result);
    }

    @Test
    void shouldAllowSkipWhenSkipCountIsBelowLimit() {// ValidationException       → true Expected :   SKIP
        boolean result = skipPolicy.shouldSkip(  new ValidationException("Invalid data"), 99 );
        assertTrue(result);
    }

    @Test
    void shouldNotAllowSkipWhenSkipCountReachesLimit() {// ValidationException       → true  Expected :   SKIP
        boolean result = skipPolicy.shouldSkip(  new ValidationException("Invalid data"), 100 );
        assertFalse(result);
    }

    @Test
    void shouldNotSkipUnknownException() {//RuntimeException          → false   Expected : DO NOT SKIP
        boolean result = skipPolicy.shouldSkip(  new RuntimeException("Unknown error"), 0 );
        assertFalse(result);
    }
}