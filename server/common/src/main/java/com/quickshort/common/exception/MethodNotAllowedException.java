package com.quickshort.common.exception;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public class MethodNotAllowedException extends CommonException {
    public MethodNotAllowedException(String reason, String message, List<FieldError> errors) {
        super(HttpStatus.METHOD_NOT_ALLOWED, reason, message, errors);
    }

    @Override
    public Map<String, Object> serializeErrors() {
        return Map.of(
                "status_code", getStatus_code(),
                "status_text", getStatus_text(),
                "success", false,
                "reason", getReason(),
                "message", getMessage(),
                "errors", getErrors().stream().map(error -> Map.of(
                        "message", error.message(),
                        "field", error.field() == null ? "" : error.field()
                )).toList()
        );
    }
}
