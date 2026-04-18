package com.medrec.exception_handling;

import com.medrec.exception_handling.exceptions.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.logging.Logger;

public class ExceptionsMapper {
    public static StatusRuntimeException toStatusRuntimeException(Throwable throwable) {
        Logger logger = Logger.getLogger(ExceptionsMapper.class.getName());
        String message = throwable.getMessage();

        if (throwable instanceof DatabaseConnectionException) {
            logger.info("Database connection exception");
            return Status.UNAVAILABLE.withDescription("Database Connection Error").asRuntimeException();
        } else if (throwable instanceof DatabaseException) {
            logger.info("Database exception");
            return Status.INTERNAL.withDescription("Database Error").asRuntimeException();
        } else if (throwable instanceof AlreadyExistsException) {
            logger.info("Already exists exception");
            return Status.ALREADY_EXISTS.withDescription("Entity Already Exists").asRuntimeException();
        } else if (throwable instanceof ConstrainException) {
            logger.info("Unique constraint exception");
            return Status.ABORTED.withDescription("Constraint Violation").asRuntimeException();
        } else if (throwable instanceof NotFoundException) {
            logger.info("Not found exception");

            if (message.contains("specialty_not_found")) {
                return Status.NOT_FOUND.withDescription("Specialty Not Found").asRuntimeException();
            }
            if (message.contains("doctor_not_found")) {
                return Status.NOT_FOUND.withDescription("Doctor Not Found").asRuntimeException();
            }
            if (message.contains("patient_not_found")) {
                return Status.NOT_FOUND.withDescription("Patient Not Found").asRuntimeException();
            }
            return Status.NOT_FOUND.withDescription("Entity Not Found").asRuntimeException();
        } else if (throwable instanceof InvalidPropertyException) {
            logger.info("Invalid property exception");
            if (message.contains("doctor_not_gp")) {
                return Status.INVALID_ARGUMENT.withDescription("Doctor Is Not Gp").asRuntimeException();
            }
            return Status.INVALID_ARGUMENT.withDescription("Invalid Property").asRuntimeException();
        }

        logger.severe("Unknown exception found: " + message);
        return Status.UNKNOWN.withDescription("Internal Server Error").asRuntimeException();
    }
}
