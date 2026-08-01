package com.cp.assignment.miniproject.common;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CommonUtils {
    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
    public static boolean isNegative(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) < 0;
    }
    // dd-MM-yyyy eg. 16-04-2025
    public static LocalDate ddMMyyyyParseDate(String value) {
        if (CommonUtils.isBlank(value )) {
            return null;
        }
        try {
            return LocalDate.parse(
                    value.trim(),
                    DateTimeFormatter.ofPattern("dd-MM-yyyy")
            );
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    // yyyy-MM-dd eg. 16-04-2025
    public static LocalDate yyyyMMddParseDate(String value) {
        if (CommonUtils.isBlank(value )) {
            return null;
        }
        try {
            return LocalDate.parse(
                    value.trim(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
            );
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
