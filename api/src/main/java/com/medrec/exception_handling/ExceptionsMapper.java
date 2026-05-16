package com.medrec.exception_handling;

import com.medrec.exception_handling.exceptions.*;
import io.grpc.StatusRuntimeException;

public class ExceptionsMapper {
    public static RuntimeException translateStatusRuntimeException(StatusRuntimeException statusRuntimeException) {
        String message = statusRuntimeException.getStatus().getDescription();
        switch (statusRuntimeException.getStatus().getCode()) {
            case UNAVAILABLE:
                return new DatabaseConnectionException(statusRuntimeException.getStatus().getDescription());
            case INTERNAL:
                return new DatabaseException(statusRuntimeException.getStatus().getDescription());
            case ALREADY_EXISTS:
                return new AlreadyExistsException(statusRuntimeException.getStatus().getDescription());
            case UNKNOWN:
                return new ServiceException(statusRuntimeException.getStatus().getDescription());
            case ABORTED:
                assert message != null;
                if (message.contains("Constraint Violation")) {
                    return new UniqueConstrainException(statusRuntimeException.getStatus().getDescription());
                } else {
                    return new BadRequestException(statusRuntimeException.getStatus().getDescription());
                }
            case NOT_FOUND:
                assert message != null;
                if (message.contains("specialty_not_found")) {
                    return new NotFoundException("Specialty Not Found");
                } else if (message.contains("doctor_not_found")) {
                    return new NotFoundException("Doctor Not Found");
                } else if (message.contains("patient_not_found")) {
                    return new NotFoundException("Patient Not Found");
                } else if (message.contains("icd_not_found")) {
                    return new NotFoundException("ICD Not Found");
                } else if (message.contains("diagnosis_not_found")) {
                    return new NotFoundException("Diagnosis Not Found");
                } else if (message.contains("sick_leave_not_found")) {
                    return new NotFoundException("Sick Leave Not Found");
                } else {
                    return new NotFoundException(statusRuntimeException.getStatus().getDescription());
                }
            case INVALID_ARGUMENT:
                assert message != null;
                if (message.contains("doctor_not_gp")) {
                    return new DoctorNotGpException("Doctor Is Not Gp");
                } else {
                    return new InvalidPropertyException(message);
                }
            case UNAUTHENTICATED:
                return new UnauthenticatedException(statusRuntimeException.getStatus().getDescription());
            case PERMISSION_DENIED:
                return new UnauthorizedException(statusRuntimeException.getStatus().getDescription());
            default:
                return new ServiceException("Service Error");
        }
    }
}
