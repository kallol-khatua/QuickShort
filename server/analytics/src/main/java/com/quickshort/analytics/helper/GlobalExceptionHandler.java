package com.quickshort.analytics.helper;

import com.quickshort.common.exception.CommonException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<Map<String, Object>> handleException(CommonException exception) {
        Map<String, Object> response = exception.serializeErrors();
        return ResponseEntity.status(exception.getStatus()).body(response);
    }
}
