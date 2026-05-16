package com.medrec.gateways;

import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
import com.google.protobuf.StringValue;
import com.medrec.exception_handling.ExceptionsMapper;
import com.medrec.grpc.users.DoctorServiceGrpc;
import com.medrec.grpc.users.PatientServiceGrpc;
import com.medrec.grpc.users.SpecialtyServiceGrpc;
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
public class UsersGateway {
    private final Logger logger = Logger.getLogger(UsersGateway.class.getName());

    private final ManagedChannel channel;

    private final DoctorServiceGrpc.DoctorServiceBlockingStub doctorService;
    private final PatientServiceGrpc.PatientServiceBlockingStub patientService;
    private final SpecialtyServiceGrpc.SpecialtyServiceBlockingStub specialtyService;

    public UsersGateway() {
        int port = Integer.parseInt(System.getenv("USERS_PORT"));
        String host = System.getenv("USERS_HOST");

        try {
            this.logger.info("Initializing Users Gateway with host: " + host + " and port: " + port);
            channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        } catch (Exception e) {
            this.logger.severe("Could not connect to Users Service");
            throw e;
        }

        doctorService = DoctorServiceGrpc.newBlockingStub(channel);
        patientService = PatientServiceGrpc.newBlockingStub(channel);
        specialtyService = SpecialtyServiceGrpc.newBlockingStub(channel);
    }

    public Users.Doctor createDoctor(Users.CreateDoctorRequest doctor) throws RuntimeException {
        try {
            return doctorService.createDoctor(doctor);
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Users.Doctor getDoctorById(int id) throws RuntimeException {
        try {
            return doctorService.getDoctorById(Int32Value.of(id));
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Users.Doctor getDoctorByEmail(String email) throws RuntimeException {
        try {
            return doctorService.getDoctorByEmail(StringValue.of(email));
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Users.DoctorList getAllDoctors() throws RuntimeException {
        try {
            return doctorService.getAllDoctors(Empty.getDefaultInstance());
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Users.DoctorList getAllGpDoctors() throws RuntimeException {
        try {
            return doctorService.getAllGPDoctors(Empty.getDefaultInstance());
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Users.Doctor updateDoctor(Users.UpdateDoctorRequest doctor) throws RuntimeException {
        try {
            return doctorService.updateDoctor(doctor);
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public void deleteDoctor(int id) throws RuntimeException {
        try {
            Empty result = doctorService.deleteDoctorById(Int32Value.of(id));
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Users.Patient createPatient(Users.CreatePatientRequest request) throws RuntimeException {
        try {
            return patientService.createPatient(request);
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Users.Patient getPatientById(int id) throws RuntimeException {
        try {
            return patientService.getPatientById(Int32Value.of(id));
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Users.Patient getPatientByEmail(String email) throws RuntimeException {
        try {
            return patientService.getPatientByEmail(StringValue.of(email));
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Users.PatientList getAllPatients() throws RuntimeException {
        try {
            return patientService.getAllPatients(Empty.getDefaultInstance());
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Users.Patient updatePatient(Users.UpdatePatientRequest patient) throws RuntimeException {
        try {
            return patientService.updatePatient(patient);
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public void deletePatient(int id) throws RuntimeException {
        try {
            Empty empty = patientService.deletePatientById(Int32Value.of(id));
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Users.Specialty createSpecialty(Users.CreateSpecialtyRequest specialty) throws RuntimeException {
        try {
            return specialtyService.createSpecialty(specialty);
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Users.Specialty getSpecialtyById(int id) throws RuntimeException {
        try {
            return specialtyService.getSpecialtyById(Int32Value.of(id));
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Users.SpecialtiesList getAllSpecialties() throws RuntimeException {
        try {
            return specialtyService.getAllSpecialties(Empty.getDefaultInstance());
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Users.Specialty updateSpecialty(Users.UpdateSpecialtyRequest specialty) throws RuntimeException {
        try {
            return specialtyService.updateSpecialty(specialty);
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public void deleteSpecialty(int id) throws RuntimeException {
        try {
            Empty empty = specialtyService.deleteSpecialtyById(Int32Value.of(id));
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Users.PatientList getAllPatientsByGpId(int gpId) throws RuntimeException {
        try {
            return patientService.getAllPatientsByGpId(Int32Value.of(gpId));
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Users.CountOfPatientsForDoctorsResponse countOfPatientsForDoctors() throws RuntimeException {
        try {
            return doctorService.countOfPatientsForDoctors(Empty.getDefaultInstance());
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Users.PatientList getPatientsByListOfIds(List<Integer> ids) throws StatusRuntimeException {
        try {
            Users.ListIdsRequest idRequest = Users.ListIdsRequest.newBuilder()
                .addAllId(ids)
                .build();
            return this.patientService.getPatientsByListOfIds(idRequest);
        } catch (StatusRuntimeException e) {
            this.logger.info("Could not find patients with ids " + ids);
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

