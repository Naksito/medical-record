package com.medrec.services;

import com.medrec.dtos.TokenDataDTO;
import com.medrec.dtos.UsersLogInRequestDTO;
import com.medrec.gateways.UsersGateway;
import com.medrec.grpc.auth.Auth;
import com.medrec.grpc.auth.AuthServiceGrpc;
import com.medrec.grpc.users.Users;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.logging.Logger;

public class AuthService extends AuthServiceGrpc.AuthServiceImplBase {
    private final Logger logger = Logger.getLogger(AuthService.class.getName());

    private static AuthService instance;

    private final JwtService jwtService = JwtService.getInstance();
    private final UsersGateway usersGateway = UsersGateway.getInstance();

    private final String ADMIN_EMAIL = System.getenv("ADMIN_EMAIL");
    private final String ADMIN_PASSWORD = System.getenv("ADMIN_PASSWORD");

    private AuthService() {}

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    @Override
    public void registerPatient(Users.CreatePatientRequest request, StreamObserver<Auth.AuthResponse> responseObserver) {
        this.logger.info("Called RPC Register Patient");

        Users.CreatePatientRequest requestWithHashedPass = Users.CreatePatientRequest.newBuilder()
            .setFirstName(request.getFirstName())
            .setLastName(request.getLastName())
            .setEmail(request.getEmail())
            .setPassword(BcryptService.hashPassword(request.getPassword()))
            .setPin(request.getPin())
            .setGpId(request.getGpId())
            .setIsHealthInsured(request.getIsHealthInsured())
            .build();

        try {
            Users.Patient savedPatient= usersGateway.registerPatient(requestWithHashedPass);

            int id = savedPatient.getId();
            String email = savedPatient.getEmail();
            String firstName = savedPatient.getFirstName();
            String lastName = savedPatient.getLastName();

            String token = jwtService.generateToken(
                id,
                firstName,
                lastName,
                email,
                "patient"
            );
            responseObserver.onNext(
                Auth.AuthResponse.newBuilder()
                    .setToken(token)
                    .setEmail(email)
                    .setId(id)
                    .setFirstName(firstName)
                    .setLastName(lastName)
                    .setRole("patient")
                    .build()
            );
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void registerDoctor(Users.CreateDoctorRequest request, StreamObserver<Auth.AuthResponse> responseObserver) {
        this.logger.info("Called RPC Register Doctor");

        Users.CreateDoctorRequest requestWithHashedPass = Users.CreateDoctorRequest.newBuilder()
            .setFirstName(request.getFirstName())
            .setLastName(request.getLastName())
            .setEmail(request.getEmail())
            .setPassword(BcryptService.hashPassword(request.getPassword()))
            .setIsGp(request.getIsGp())
            .setSpecialtyId(request.getSpecialtyId())
            .build();

        try {
            Users.Doctor savedDoctor = usersGateway.registerDoctor(requestWithHashedPass);

            int id = savedDoctor.getId();
            String email = savedDoctor.getEmail();
            String firstName = savedDoctor.getFirstName();
            String lastName = savedDoctor.getLastName();

            String token = jwtService.generateToken(
                id,
                firstName,
                lastName,
                email,
                "doctor"
            );
            responseObserver.onNext(
                Auth.AuthResponse.newBuilder()
                    .setToken(token)
                    .setEmail(email)
                    .setId(id)
                    .setFirstName(firstName)
                    .setLastName(lastName)
                    .setRole("doctor")
                    .build()
            );
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void logPatientIn(Auth.LoginRequest request,StreamObserver<Auth.AuthResponse> responseObserver ) {
        this.logger.info("Called RPC Log Patient In");

        String email = request.getEmail();
        String password = request.getPassword();

        UsersLogInRequestDTO requestDTO = new UsersLogInRequestDTO(email, password);

        try {
            Users.Patient savedPatient = usersGateway.getPatientByEmail(requestDTO);

            int id = savedPatient.getId();
            String firstName = savedPatient.getFirstName();
            String lastName = savedPatient.getLastName();
            String email1 = savedPatient.getEmail();

            if(BcryptService.checkPassword(password, savedPatient.getPassword())) {
                String token = jwtService.generateToken(
                    id,
                    firstName,
                    lastName,
                    email,
                    "patient"
                );
                responseObserver.onNext(
                    Auth.AuthResponse.newBuilder()
                        .setToken(token)
                        .setEmail(email)
                        .setId(id)
                        .setFirstName(firstName)
                        .setLastName(lastName)
                        .setRole("patient")
                        .build()
                );
                responseObserver.onCompleted();
            } else {
                this.logger.warning("Invalid credentials for patient: " + email);
                responseObserver.onError(Status.UNAUTHENTICATED.withDescription("Invalid credentials").asRuntimeException());
            }
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void logDoctorIn(Auth.LoginRequest request,StreamObserver<Auth.AuthResponse> responseObserver ) {
        this.logger.info("Called RPC Log Doctor In");

        String email = request.getEmail();
        String password = request.getPassword();

        UsersLogInRequestDTO requestDTO = new UsersLogInRequestDTO(email, password);

        try {
            Users.Doctor savedDoctor = usersGateway.getDoctorByEmail(requestDTO);

            int id = savedDoctor.getId();
            String firstName = savedDoctor.getFirstName();
            String lastName = savedDoctor.getLastName();

            if(BcryptService.checkPassword(password, savedDoctor.getPassword())) {
                String token = jwtService.generateToken(
                    id,
                    firstName,
                    lastName,
                    email,
                    "doctor"
                );
                responseObserver.onNext(
                    Auth.AuthResponse.newBuilder()
                        .setToken(token)
                        .setEmail(email)
                        .setId(id)
                        .setFirstName(firstName)
                        .setLastName(lastName)
                        .setRole("doctor")
                        .build());
                responseObserver.onCompleted();
            } else {
                this.logger.warning("Invalid credentials for doctor: " + email);
                responseObserver.onError(Status.UNAUTHENTICATED.withDescription("Invalid credentials").asRuntimeException());
            }
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void logAdminIn(Auth.LoginRequest request,StreamObserver<Auth.AuthResponse> responseObserver ) {
        this.logger.info("Called RPC Log Admin In");

        String email = request.getEmail();
        String password = request.getPassword();

        if (email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)) {
            String token = jwtService.generateToken(
                1,
                "Admin",
                "Admin",
                email,
                "admin"
            );
            responseObserver.onNext(
                Auth.AuthResponse.newBuilder()
                    .setToken(token)
                    .setEmail(email)
                    .setId(1)
                    .setFirstName("Admin")
                    .setLastName("Admin")
                    .setRole("admin")
                    .build());
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.UNAUTHENTICATED.withDescription("Invalid credentials").asRuntimeException());
        }
    }

    @Override
    public void authorizeRequest(Auth.AuthorizationRequest request, StreamObserver<Auth.AuthorizationResponse> responseObserver) {
        this.logger.info("Called RPC Authorize Request");

        String token = request.getToken();
        List<String> requiredRole = request.getRequiredRolesList();

        if(token.isBlank()) {
            responseObserver.onError(Status.ABORTED.withDescription("Bad request").asRuntimeException());
            return;
        }

        if (requiredRole.isEmpty()) {
            if(this.jwtService.isTokenValid(token)) {
                responseObserver.onNext(
                    Auth.AuthorizationResponse.newBuilder()
                        .setIsTokenAuthorized(true)
                        .build()
                );
                responseObserver.onCompleted();
            } else {
                responseObserver.onError(Status.PERMISSION_DENIED.withDescription("Unauthorized").asRuntimeException());
            }
            return;
        }

        boolean isAuthorized = jwtService.isUserAuthorized(token, requiredRole);

        if(isAuthorized) {
            responseObserver.onNext(
                Auth.AuthorizationResponse.newBuilder()
                    .setIsTokenAuthorized(true)
                    .build()
            );
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.PERMISSION_DENIED.withDescription("Unauthorized").asRuntimeException());
        }
    }

    @Override
    public void validateToken(Auth.TokenRequest request, StreamObserver<Auth.AuthResponse> responseObserver) {
        this.logger.info("Called RPC Validate Token");

        String token = request.getToken();

        if (token.isBlank()) {
            responseObserver.onError(Status.ABORTED.withDescription("Bad request").asRuntimeException());
            return;
        }

        boolean isValid = this.jwtService.isTokenValid(token);

        if (isValid) {
            TokenDataDTO data = this.jwtService.getDataFromToken(token);
            this.logger.info(data.toString());
            responseObserver.onNext(
                Auth.AuthResponse.newBuilder()
                    .setId(data.getId())
                    .setEmail(data.getEmail())
                    .setFirstName(data.getFirstName())
                    .setLastName(data.getLastName())
                    .setRole(data.getRole())
                    .build()
            );
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.PERMISSION_DENIED.withDescription("Invalid token").asRuntimeException());
        }
    }
}
