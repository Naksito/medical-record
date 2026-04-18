package com.medrec.services;

import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
import com.google.protobuf.StringValue;
import com.medrec.dtos.CreatePatientDTO;
import com.medrec.exception_handling.ExceptionsMapper;
import com.medrec.exception_handling.exceptions.DoctorIsNotGpException;
import com.medrec.grpc.users.PatientServiceGrpc;
import com.medrec.grpc.users.Users;
import com.medrec.persistence.patient.Patient;
import com.medrec.persistence.patient.PatientRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.logging.Logger;

public class PatientService extends PatientServiceGrpc.PatientServiceImplBase {
    private static PatientService instance;

    private final Logger logger = Logger.getLogger(PatientService.class.getName());

    private final PatientRepository patientRepository = PatientRepository.getInstance();

    private PatientService() {
    }

    public static PatientService getInstance() {
        if (instance == null) {
            instance = new PatientService();
        }
        return instance;
    }

    @Override
    public void createPatient(Users.CreatePatientRequest request, StreamObserver<Users.Patient> responseObserver) {
        this.logger.info("Called RPC Create Patient - Doctor Id");
        CreatePatientDTO dto = new CreatePatientDTO(
            request.getFirstName(),
            request.getLastName(),
            request.getEmail(),
            request.getPassword(),
            request.getPin(),
            request.getGpId(),
            request.getIsHealthInsured()
        );

        try {
            Patient patient = patientRepository.save(dto);
            responseObserver.onNext(grpcFromDomainModel(patient));
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void getPatientById(Int32Value request, StreamObserver<Users.Patient> responseObserver) {
        this.logger.info("Called RPC Get Patient By Id");
        int id = request.getValue();

        try {
            Patient patient = patientRepository.findById(id);
            responseObserver.onNext(grpcFromDomainModel(patient));
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void getPatientByEmail(StringValue request, StreamObserver<Users.Patient> responseObserver) {
        this.logger.info("Called RPC Get Patient By Email");
        String email = request.getValue();

        try {
            Patient patient = patientRepository.findByEmail(email);
            responseObserver.onNext(grpcFromDomainModel(patient));
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void getAllPatients(Empty request, StreamObserver<Users.PatientList> responseObserver) {
        this.logger.info("Called RPC Get All Patients");

        try {
            List<Patient> patients = patientRepository.findAll();
            List<Users.Patient> grpcPatientsList = patients.stream()
                .map(PatientService::grpcFromDomainModel)
                .toList();
            Users.PatientList patientList = Users.PatientList.newBuilder()
                .addAllPatients(grpcPatientsList)
                .build();

            responseObserver.onNext(patientList);
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void updatePatient(Users.UpdatePatientRequest request, StreamObserver<Users.Patient> responseObserver) {
        this.logger.info("Called RPC Update Patient");

        try {
            Patient updatedPatient = patientRepository.update(request);
            responseObserver.onNext(grpcFromDomainModel(updatedPatient));
            responseObserver.onCompleted();
        } catch (DoctorIsNotGpException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("doctor_not_gp").asRuntimeException());
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deletePatientById(Int32Value request, StreamObserver<Empty> responseObserver) {
        this.logger.info("Called RPC Delete Patient");
        int id = request.getValue();

        try {
            patientRepository.delete(id);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void getAllPatientsByGpId(Int32Value request, StreamObserver<Users.PatientList> responseObserver) {
        int id = request.getValue();
        this.logger.info("Called RPC Query All Patients By GP id: " + id);

        try {
            List<Patient> patients = patientRepository.getAllPatientsByGpId(id);
            List<Users.Patient> grpcPatientsList = patients.stream()
                .map(PatientService::grpcFromDomainModel)
                .toList();
            Users.PatientList patientList = Users.PatientList.newBuilder()
                .addAllPatients(grpcPatientsList)
                .build();

            responseObserver.onNext(patientList);
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void getPatientsByListOfIds(Users.ListIdsRequest request, StreamObserver<Users.PatientList> responseObserver) {
        List<Integer> ids= request.getIdList();
        this.logger.info(String.format("Called RPC Get Patients By List of IDs for %d IDs", ids.size()));

        try {
            List<Patient> patients = patientRepository.getPatientsByListOfIds(ids);
            List<Users.Patient> grpcPatientsList = patients.stream()
                .map(PatientService::grpcFromDomainModel)
                .toList();
            Users.PatientList patientList = Users.PatientList.newBuilder()
                .addAllPatients(grpcPatientsList)
                .build();

            responseObserver.onNext(patientList);
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    private static Users.Patient grpcFromDomainModel(Patient patient) {
        Users.Doctor gp = DoctorService.grpcDoctorFromDomainModel(patient.getDoctor());

        return Users.Patient.newBuilder()
            .setId(patient.getId())
            .setFirstName(patient.getFirstName())
            .setLastName(patient.getLastName())
            .setEmail(patient.getEmail())
            .setPassword(patient.getPassword())
            .setPin(patient.getPin())
            .setGp(gp)
            .setIsHealthInsured(patient.isHealthInsured())
            .build();
    }
}
