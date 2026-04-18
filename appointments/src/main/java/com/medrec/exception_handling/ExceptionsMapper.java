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
        } else if (throwable instanceof BadRequestException) {
            logger.info("Bad request exception");
            if(message.contains("date_time_not_parsed")) {
                return Status.ABORTED.withDescription("Datetime not parsed correctly").asRuntimeException();
            } else if (message.contains("doctor_not_free")) {
                return Status.ABORTED.withDescription("Doctor is not free at the specified time").asRuntimeException();
            } else if(message.contains("invalid_id")) {
                return Status.ABORTED.withDescription("Invalid id").asRuntimeException();
            } else if (message.contains("invalid_status")) {
                return Status.ABORTED.withDescription("Invalid status").asRuntimeException();
            } else if (message.contains("invalid_entity_type")) {
                return Status.ABORTED.withDescription("Invalid entity type").asRuntimeException();
            } else if (message.contains("invalid_treatment_description_or_icd_id")) {
                return Status.ABORTED.withDescription("Invalid treatment description or ICD id").asRuntimeException();
            } else if (message.contains("invalid_code_or_description")) {
                return Status.ABORTED.withDescription("Invalid code or description").asRuntimeException();
            } else if (message.contains("invalid_start_date_or_number_of_days_leave")) {
                return Status.ABORTED.withDescription("Invalid start date or number of days leave").asRuntimeException();
            } else if (message.contains("invalid_number_of_days_leave")) {
                return Status.ABORTED.withDescription("Invalid number of days leave").asRuntimeException();
            } else {
                return Status.ABORTED.withDescription("Bad request").asRuntimeException();
            }
        } else if (throwable instanceof NotFoundException) {
            logger.info("Not found exception");
            if (message.contains("appointment_not_found")) {
                return Status.NOT_FOUND.withDescription("Appointment Not Found").asRuntimeException();
            } else if (message.contains("patient_not_found")) {
                return Status.NOT_FOUND.withDescription("Patient Not Found").asRuntimeException();
            } else if (message.contains("doctor_not_found")) {
                return Status.NOT_FOUND.withDescription("Doctor Not Found").asRuntimeException();
            } else if (message.contains("icd_not_found")) {
                return Status.NOT_FOUND.withDescription("ICD Not Found").asRuntimeException();
            } else if (message.contains("sick_leave_not_found")) {
                return Status.NOT_FOUND.withDescription("Sick Leave Not Found").asRuntimeException();
            } else if (message.contains("diagnosis_not_found")) {
                return Status.NOT_FOUND.withDescription("Diagnosis Not Found").asRuntimeException();
            } else {
                return Status.NOT_FOUND.withDescription("Entity Not Found").asRuntimeException();
            }
        } else if (throwable instanceof AbortedException) {
            logger.info("Aborted exception");
            if (message.contains("doctor_has_no_access_to_appointment")) {
                return Status.ABORTED.withDescription("Doctor has no access to Appointment").asRuntimeException();
            } else {
                return Status.ABORTED.withDescription("Aborted exception").asRuntimeException();
            }
        }

        logger.severe("Unknown exception found: " + message);
        return Status.UNKNOWN.withDescription("Internal Server Error").asRuntimeException();
    }
}
