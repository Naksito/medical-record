package com.medrec.exception_handling.exceptions;

public class MissingPropertyException extends RuntimeException {
    public MissingPropertyException(String message) {
        super(message);
    }
}
