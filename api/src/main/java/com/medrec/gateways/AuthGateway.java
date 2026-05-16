package com.medrec.gateways;

import com.medrec.dtos.auth.AuthResponseDTO;
import com.medrec.exception_handling.ExceptionsMapper;
import com.medrec.grpc.auth.Auth;
import com.medrec.grpc.auth.AuthServiceGrpc;
import com.medrec.grpc.users.Users;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Component
public class AuthGateway {
    private final Logger logger = Logger.getLogger(AuthGateway.class.getName());

    private final ManagedChannel channel;
    private final AuthServiceGrpc.AuthServiceBlockingStub authService;

    public AuthGateway() {
        int port = Integer.parseInt(System.getenv("AUTH_PORT"));
        String host = System.getenv("AUTH_HOST");

        try {
            this.logger.info("Initializing Auth Gateway with host: " + host + " and port: " + port);
            channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        } catch (Exception e) {
            this.logger.severe("Could not connect to Users Service");
            throw e;
        }

        authService = AuthServiceGrpc.newBlockingStub(channel);
    }

    public AuthResponseDTO registerDoctor(
        String firstName,
        String lastName,
        String email,
        String password,
        boolean isGp,
        int specialtyId
    ) throws RuntimeException {
        try {
            Users.CreateDoctorRequest doctor = Users.CreateDoctorRequest.newBuilder()
                .setFirstName(firstName)
                .setLastName(lastName)
                .setEmail(email)
                .setPassword(password)
                .setIsGp(isGp)
                .setSpecialtyId(specialtyId)
                .build();

            Auth.AuthResponse response = authService.registerDoctor(doctor);

            return new AuthResponseDTO(
                response.getToken(),
                response.getId(),
                response.getEmail(),
                response.getFirstName(),
                response.getLastName(),
                response.getRole()
            );
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public AuthResponseDTO registerPatient(
        String firstName,
        String lastName,
        String email,
        String password,
        String pin,
        int generalPractitionerId,
        boolean isHealthInsured
    ) throws RuntimeException {
        try {
            Users.CreatePatientRequest patient = Users.CreatePatientRequest.newBuilder()
                .setFirstName(firstName)
                .setLastName(lastName)
                .setEmail(email)
                .setPassword(password)
                .setPin(pin)
                .setGpId(generalPractitionerId)
                .setIsHealthInsured(isHealthInsured)
                .build();

            Auth.AuthResponse response = authService.registerPatient(patient);

            return new AuthResponseDTO(
                response.getToken(),
                response.getId(),
                response.getEmail(),
                response.getFirstName(),
                response.getLastName(),
                response.getRole()
            );
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public AuthResponseDTO logPatientIn(String email, String password) throws RuntimeException {
        try {
            Auth.AuthResponse response = authService.logPatientIn(
                Auth.LoginRequest.newBuilder()
                    .setEmail(email)
                    .setPassword(password)
                    .build()
            );

            return new AuthResponseDTO(
                response.getToken(),
                response.getId(),
                response.getEmail(),
                response.getFirstName(),
                response.getLastName(),
                response.getRole()
            );
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public AuthResponseDTO logDoctorIn(String email, String password) throws RuntimeException{
        try {
            Auth.AuthResponse response = authService.logDoctorIn(
                Auth.LoginRequest.newBuilder()
                    .setEmail(email)
                    .setPassword(password)
                    .build()
            );

            return new AuthResponseDTO(
                response.getToken(),
                response.getId(),
                response.getEmail(),
                response.getFirstName(),
                response.getLastName(),
                response.getRole()
            );
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public AuthResponseDTO logAdminIn(String email, String password) throws RuntimeException {
        try {
            Auth.AuthResponse response = authService.logAdminIn(
                Auth.LoginRequest.newBuilder()
                    .setEmail(email)
                    .setPassword(password)
                    .build()
            );

            return new AuthResponseDTO(
                response.getToken(),
                response.getId(),
                response.getEmail(),
                response.getFirstName(),
                response.getLastName(),
                response.getRole()
            );
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public boolean isRequestAuthorized(String token, List<String> requiredRoles) throws RuntimeException {
        try {
            Auth.AuthorizationRequest request = Auth.AuthorizationRequest.newBuilder()
                .setToken(token)
                .addAllRequiredRoles(requiredRoles)
                .build();

            Auth.AuthorizationResponse response = authService.authorizeRequest(request);

            return response.getIsTokenAuthorized();
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public AuthResponseDTO isTokenValid(String token) throws RuntimeException {
        try {
            Auth.AuthResponse response = authService.validateToken(
                Auth.TokenRequest.newBuilder()
                .setToken(token)
                .build()
            );

            return new AuthResponseDTO(
                response.getToken(),
                response.getId(),
                response.getEmail(),
                response.getFirstName(),
                response.getLastName(),
                response.getRole()
            );
        } catch (StatusRuntimeException e){
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    @PreDestroy
    public void shutdown() {
        logger.info("Shutting down channel");
        try {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            channel.shutdownNow();
        }
    }
}
