package com.medrec.exception_handling;

import com.medrec.exception_handling.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalHTTPExceptionHandler {
    @ExceptionHandler(DatabaseConnectionException.class)
    public ResponseEntity<ErrorHTTPResponse> handleDatabaseConnectionException(DatabaseConnectionException e) {
        ErrorHTTPResponse error = new ErrorHTTPResponse("DATABASE_CONNECTION_ERROR", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorHTTPResponse> handleHibernateException(DatabaseException e) {
        ErrorHTTPResponse error = new ErrorHTTPResponse("HIBERNATE_EXCEPTION", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorHTTPResponse> handleAlreadyExistsException(AlreadyExistsException e) {
        ErrorHTTPResponse error = new ErrorHTTPResponse("ALREADY_EXISTS_EXCEPTION", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorHTTPResponse> handleServiceException(ServiceException e) {
        ErrorHTTPResponse error = new ErrorHTTPResponse("SERVICE_EXCEPTION", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorHTTPResponse> handleUniqueConstrainException(UniqueConstrainException e) {
        ErrorHTTPResponse error = new ErrorHTTPResponse("UNIQUE_CONSTRAIN_EXCEPTION", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorHTTPResponse> handleNotFound(NotFoundException e) {
        String message = e.getMessage();
        if (message.contains("specialty_not_found")) {
            ErrorHTTPResponse error = new ErrorHTTPResponse("SPECIALTY_NOT_FOUND_EXCEPTION", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        } else if (message.contains("doctor_not_found")) {
            ErrorHTTPResponse error = new ErrorHTTPResponse("DOCTOR_NOT_FOUND_EXCEPTION", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        } else if (message.contains("patient_not_found")) {
            ErrorHTTPResponse error = new ErrorHTTPResponse("PATIENT_NOT_FOUND_EXCEPTION", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        } else {
            ErrorHTTPResponse error = new ErrorHTTPResponse("NOT_FOUND_EXCEPTION", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

    @ExceptionHandler
    public ResponseEntity<ErrorHTTPResponse> handleDoctorNotGpException(DoctorNotGpException e) {
        ErrorHTTPResponse error = new ErrorHTTPResponse("DOCTOR_NOT_GP_EXCEPTION", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorHTTPResponse> handleInvalidPropertyException(InvalidPropertyException e) {
        ErrorHTTPResponse error = new ErrorHTTPResponse("INVALID_PROPERTY_EXCEPTION", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorHTTPResponse> handleUnauthenticatedException(UnauthenticatedException e) {
        ErrorHTTPResponse error = new ErrorHTTPResponse("UNAUTHENTICATED_EXCEPTION", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorHTTPResponse> handleIdNotSetException(IdNotSetException e) {
        ErrorHTTPResponse error = new ErrorHTTPResponse("ID_NOT_SET_EXCEPTION", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorHTTPResponse> handleUnauthorizedException(UnauthorizedException e) {
        ErrorHTTPResponse error = new ErrorHTTPResponse("UNAUTHORIZED_EXCEPTION", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorHTTPResponse> handleBadRequestException(BadRequestException e) {
        ErrorHTTPResponse error = new ErrorHTTPResponse("BAD_REQUEST_EXCEPTION", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorHTTPResponse> handleConcurrentException(ConcurrentException e) {
        ErrorHTTPResponse error = new ErrorHTTPResponse("CONCURRENT_EXCEPTION", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
