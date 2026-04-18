package com.medrec.exception_handling.exceptions;

public class AbortedException extends RuntimeException {
    public AbortedException(String message) {
        super(message);
    }
}
