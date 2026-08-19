package com.cp.assignment.miniproject.common;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CommonUtils {
    // Sentinel value used only to represent invalid decimal format.
    // This value is outside the expected transaction amount range.
    private static final BigDecimal INVALID_DECIMAL =  new BigDecimal("-999999999999999999999999999999999999.999999");

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

    public static BigDecimal parseDecimal(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return new BigDecimal(value.replace(",", ""));
        } catch (NumberFormatException e) {
            return INVALID_DECIMAL;
        }
    }

    public static boolean isInvalidDecimal(BigDecimal value) {
        return value != null   && value.compareTo(INVALID_DECIMAL) == 0;
    }
    public static String csv(String value) {

        if (value == null) {
            return "";
        }

        String escaped = value.replace("\"", "\"\"");

        if (escaped.contains(",")
                || escaped.contains("\"")
                || escaped.contains("\n")) {

            return "\"" + escaped + "\"";
        }

        return escaped;
    }

    public static String decimal(BigDecimal value) {
        return value == null ? "" : value.toPlainString();
    }
}
