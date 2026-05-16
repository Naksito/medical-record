package com.medrec.exception_handling.exceptions;

public class ConcurrentException extends RuntimeException {
    public ConcurrentException(String message) {
        super(message);
    }
}
