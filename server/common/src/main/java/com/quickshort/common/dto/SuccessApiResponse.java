package com.quickshort.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SuccessApiResponse<T> {
    private int status_code;
    private String status_text;
    private boolean success;
    private String status;
    private String message;
    private T data;
}