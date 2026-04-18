package com.medrec.gateway;

import com.google.protobuf.Int32Value;
import com.google.protobuf.StringValue;
import com.medrec.grpc.users.DoctorServiceGrpc;
import com.medrec.grpc.users.PatientServiceGrpc;
import com.medrec.grpc.users.Users;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class UsersGateway {
    private static UsersGateway instance;

    private final Logger logger = Logger.getLogger(UsersGateway.class.getName());
    private final ManagedChannel channel;

    private final PatientServiceGrpc.PatientServiceBlockingStub patientService;
    private final DoctorServiceGrpc.DoctorServiceBlockingStub doctorService;

    private UsersGateway() {
        int port = Integer.parseInt(System.getenv("USERS_PORT"));
        String host = System.getenv("USERS_HOST");

        try {
            this.logger.info("Initializing Users Gateway with host: " + host + " and port: " + port);
            channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        } catch (Exception e) {
            this.logger.severe("Could not connect to Users Service");
            throw e;
        }

        patientService = PatientServiceGrpc.newBlockingStub(channel);
        doctorService = DoctorServiceGrpc.newBlockingStub(channel);
    }

    public static UsersGateway getInstance() {
        if (instance == null) {
            instance = new UsersGateway();
        }
        return instance;
    }

    public void patientExists(int id) throws StatusRuntimeException {
        try {
            Users.Patient patient = this.patientService.getPatientById(Int32Value.of(id));
        } catch (StatusRuntimeException e) {
            this.logger.info("Could not find patient with id " + id);
            throw e;
        }
    }

    public void doctorExists(int id) throws StatusRuntimeException {
        try {
            Users.Doctor doctor = this.doctorService.getDoctorById(Int32Value.of(id));
        } catch (StatusRuntimeException e) {
            this.logger.info("Could not find doctor with id " + id);
            throw e;
        }
    }

    public int getPatientIdByEmail(String email) throws StatusRuntimeException {
        try {
            Users.Patient patient = this.patientService.getPatientByEmail(StringValue.of(email));
            return patient.getId();
        } catch (StatusRuntimeException e) {
            this.logger.info("Could not find patient with email " + email);
            throw e;
        }
    }

    public int getDoctorIdByEmail(String email) throws StatusRuntimeException {
        try {
            Users.Doctor doctor = this.doctorService.getDoctorByEmail(StringValue.of(email));
            return doctor.getId();
        } catch (StatusRuntimeException e) {
            this.logger.info("Could not find doctor with email " + email);
            throw e;
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
