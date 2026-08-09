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
   /* ValidationException       → true
    DateTimeParseException    → true
    NumberFormatException     → true

    SQLException              → false
    IOException               → false
    NullPointerException      → false
    RuntimeException          → false*/

    @BeforeEach
    void setUp() {
        skipPolicy = new ReconciliationSkipPolicy();
    }

    @Test
    void shouldSkipValidationException() {
        boolean result = skipPolicy.shouldSkip(
                new ValidationException("Order number is required"),
                //new ValidationException("Invalid data"),
                0
        );

        assertTrue(result);
    }

    @Test
    void shouldSkipDateTimeParseException() {
        boolean result = skipPolicy.shouldSkip(
                new DateTimeParseException(   "Invalid date",  "2025-99-99",   0  ), 0 );
        assertTrue(result);
    }

    @Test
    void shouldSkipNumberFormatException() {
        boolean result = skipPolicy.shouldSkip(  new NumberFormatException("Invalid number"), 0 );
        assertTrue(result);
    }

    @Test
    void shouldNotSkipSQLException() {
        boolean result = skipPolicy.shouldSkip(   new SQLException("Database error"), 0  );
        assertFalse(result);
    }

    @Test
    void shouldNotSkipIOException() {
        boolean result = skipPolicy.shouldSkip( new IOException("File error"), 0 );
        assertFalse(result);
    }

    @Test
    void shouldNotSkipNullPointerException() {
        boolean result = skipPolicy.shouldSkip(  new NullPointerException("Programming error"), 0  );
        assertFalse(result);
    }

    @Test
    void shouldAllowSkipWhenSkipCountIsBelowLimit() {
        boolean result = skipPolicy.shouldSkip(  new ValidationException("Invalid data"), 99 );
        assertTrue(result);
    }

    @Test
    void shouldNotAllowSkipWhenSkipCountReachesLimit() {
        boolean result = skipPolicy.shouldSkip(  new ValidationException("Invalid data"), 100 );
        assertFalse(result);
    }

    @Test
    void shouldNotSkipUnknownException() {
        boolean result = skipPolicy.shouldSkip(  new RuntimeException("Unknown error"), 0 );
        assertFalse(result);
    }
}