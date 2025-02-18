package com.quickshort.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
public abstract class CommonException extends RuntimeException {

    private final HttpStatus status;
    private final int status_code;
    private final String status_text;
    private final boolean success;
    private final String reason;
    private final String message;
    private final List<FieldError> errors;

    public CommonException(HttpStatus status, String reason, String message, List<FieldError> errors) {
        super(message);
        this.status = status;
        this.status_code = status.value();
        this.status_text = status.name();
        this.success = false;
        this.reason = reason;
        this.message = message;
        this.errors = errors != null ? errors : Collections.emptyList();
    }

    public abstract Map<String, Object> serializeErrors();
}
