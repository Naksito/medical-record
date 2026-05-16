package com.medrec.exception_handling.exceptions;

public class UniqueConstrainException extends RuntimeException {
    public UniqueConstrainException(String message) {
        super(message);
    }
}
