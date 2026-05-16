package com.medrec.services;

import com.medrec.dtos.auth.AuthResponseDTO;
import com.medrec.dtos.auth.LogUserInDTO;
import com.medrec.dtos.users.doctor.RegisterDoctorDTO;
import com.medrec.dtos.users.patient.RegisterPatientDTO;
import com.medrec.gateways.AuthGateway;
import io.grpc.StatusRuntimeException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class AuthService {
    private final Logger logger = Logger.getLogger(AuthService.class.getName());

    private final AuthGateway authGateway;

    public AuthService(AuthGateway authGateway) {
        this.authGateway = authGateway;
    }

    public AuthResponseDTO registerDoctor(RegisterDoctorDTO dto) throws RuntimeException {
        this.logger.info("Trying to register doctor with email: " + dto.getEmail());
        try {
            AuthResponseDTO response = this.authGateway.registerDoctor(
                dto.getFirstName(),
                dto.getLastName(),
                dto.getEmail(),
                dto.getPassword(),
                dto.isGeneralPractitioner(),
                dto.getSpecialtyId()
            );

            this.logger.info("Doctor registered with email: " + dto.getEmail());
            return response;
        } catch (StatusRuntimeException e) {
            this.logger.warning("Could not register doctor with email: " + dto.getEmail());
            throw e;
        }
    }

    public AuthResponseDTO registerPatient(RegisterPatientDTO dto) throws RuntimeException {
        this.logger.info("Trying to register patient with email: " + dto.getEmail());
        try {
            AuthResponseDTO response = this.authGateway.registerPatient(
                dto.getFirstName(),
                dto.getLastName(),
                dto.getEmail(),
                dto.getPassword(),
                dto.getPin(),
                dto.getGpId(),
                dto.isInsured()
            );
            this.logger.info("Patient registered with email: " + dto.getEmail());
            return response;
        } catch (StatusRuntimeException e) {
            this.logger.warning("Could not register patient with email: " + dto.getEmail());
            throw e;
        }
    }

    public AuthResponseDTO logDoctorIn(LogUserInDTO dto) throws RuntimeException {
        this.logger.info("Trying to log in doctor with email: " + dto.getEmail());
        try {
            this.logger.info("Doctor logged in with email: " + dto.getEmail());
            return authGateway.logDoctorIn(dto.getEmail(), dto.getPassword());
        } catch (StatusRuntimeException e) {
            this.logger.warning("Could not log in doctor with email: " + dto.getEmail());
            throw e;
        }
    }

    public AuthResponseDTO logPatientIn(LogUserInDTO dto) throws StatusRuntimeException {
        this.logger.info("Trying to log in patient with email: " + dto.getEmail());
        try {
            this.logger.info("Patient logged in with email: " + dto.getEmail());
            return authGateway.logPatientIn(dto.getEmail(), dto.getPassword());
        } catch (StatusRuntimeException e) {
            this.logger.warning("Could not log in patient with email: " + dto.getEmail());
            throw e;
        }
    }

    public AuthResponseDTO logAdminIn(LogUserInDTO dto) throws StatusRuntimeException {
        this.logger.info("Trying to log in admin with email: " + dto.getEmail());
        try {
            this.logger.info("Admin logged in with email: " + dto.getEmail());
            return authGateway.logAdminIn(dto.getEmail(), dto.getPassword());
        } catch (StatusRuntimeException e) {
            this.logger.warning("Could not log in admin with email: " + dto.getEmail());
            throw e;
        }
    }

    public boolean isRequestAuthorized(String token, List<String> requiredRoles) throws StatusRuntimeException {
        this.logger.info("Checking if request is authorized for roles: " + requiredRoles.toString());
        try {
            boolean response = authGateway.isRequestAuthorized(token, requiredRoles);
            this.logger.info("Request is authorized for roles: " + requiredRoles);
            return response;
        } catch (StatusRuntimeException e) {
            this.logger.warning("Could not check if request is authorized for roles: " + requiredRoles);
            throw e;
        }
    }

    public AuthResponseDTO isTokenValid(String token) throws StatusRuntimeException{
        this.logger.info("Checking if token is valid");
        try {
            this.logger.info("Token is valid");
            return authGateway.isTokenValid(token);
        } catch (StatusRuntimeException e) {
            this.logger.warning("Could not check if token is valid");
            throw e;
        }
    }
}
