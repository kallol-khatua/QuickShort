package com.quickshort.common.exception;

public record FieldError(String message, String field) {
    public FieldError(String message) {
        this(message, null);
    }
}
