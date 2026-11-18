package ua.nupp.defects.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Check {

    public static void notEmpty(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static LocalDate date(String value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(message);
        }
    }
}
