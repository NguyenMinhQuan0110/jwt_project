package com.example.demo.exception;

import java.util.List;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    private final Integer status;
    private final List<String> errors;

    public AppException(Integer status, String message) {
        super(message);
        this.status = status;
        this.errors = null;
    }

    public AppException(Integer status, String message, List<String> errors) {
        super(message);
        this.status = status;
        this.errors = errors;
    }
}
