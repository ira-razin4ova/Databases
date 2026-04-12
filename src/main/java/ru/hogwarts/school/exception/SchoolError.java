package ru.hogwarts.school.exception;

import java.time.LocalDateTime;

public class SchoolError {
    private final String code;
    private final String message;
    private final LocalDateTime timestamp;

    public SchoolError(String code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();

    }

    public String getCode() {
        return code;
    }
    public String getMessage () {
        return message;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

