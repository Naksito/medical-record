package com.medrec.services;

import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
import com.google.protobuf.StringValue;
import com.medrec.dtos.CreateDoctorDTO;
import com.medrec.dtos.PatientCountDTO;
import com.medrec.exception_handling.ExceptionsMapper;
import com.medrec.grpc.users.DoctorServiceGrpc;
import com.medrec.grpc.users.Users;
import com.medrec.persistence.doctor.Doctor;
import com.medrec.persistence.doctor.DoctorRepository;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.logging.Logger;

public class DoctorService extends DoctorServiceGrpc.DoctorServiceImplBase {
    private static DoctorService instance;

    private final Logger logger = Logger.getLogger(DoctorService.class.getName());

    private final DoctorRepository doctorRepository = DoctorRepository.getInstance();

    private DoctorService() {
    }

    public static DoctorService getInstance() {
        if (instance == null) {
            instance = new DoctorService();
        }
        return instance;
    }

    @Override
    public void createDoctor(Users.CreateDoctorRequest request, StreamObserver<Users.Doctor> responseObserver) {
        this.logger.info("Called RPC Create Doctor- Specialty Id");
        CreateDoctorDTO dto = new CreateDoctorDTO(
            request.getFirstName(),
            request.getLastName(),
            request.getEmail(),
            request.getPassword(),
            request.getSpecialtyId(),
            request.getIsGp()
        );

        try {
            Doctor savedDoctor = doctorRepository.save(dto);
            responseObserver.onNext(grpcDoctorFromDomainModel(savedDoctor));
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void getDoctorById(Int32Value request, StreamObserver<Users.Doctor> responseObserver) {
        this.logger.info("Called RPC Get Doctor By Id");
        int id = request.getValue();

        try {
            Doctor doctor = doctorRepository.findById(id);
            responseObserver.onNext(grpcDoctorFromDomainModel(doctor));
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void getDoctorByEmail(StringValue request, StreamObserver<Users.Doctor> responseObserver) {
        this.logger.info("Called RPC Get Doctor By Email");
        String email = request.getValue();

        try {
            Doctor doctor = doctorRepository.findByEmail(email);
            responseObserver.onNext(grpcDoctorFromDomainModel(doctor));
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void getAllGPDoctors(Empty request, StreamObserver<com.medrec.grpc.users.Users.DoctorList> responseObserver) {
        this.logger.info("Called RPC Get All GP Doctors");

        try {
            List<Doctor> doctors = doctorRepository.findAllGpDoctors();
            List<Users.Doctor> grpcDoctors = doctors.stream()
                .map(DoctorService::grpcDoctorFromDomainModel)
                .toList();
            Users.DoctorList list = Users.DoctorList.newBuilder()
                .addAllDoctors(grpcDoctors)
                .build();

            responseObserver.onNext(list);
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void getAllDoctors(Empty request, StreamObserver<com.medrec.grpc.users.Users.DoctorList> responseObserver) {
        this.logger.info("Called RPC Get All Doctors");

        try {
            List<Doctor> doctors = doctorRepository.findAll();
            List<Users.Doctor> grpcDoctorsList = doctors.stream()
                .map(DoctorService::grpcDoctorFromDomainModel)
                .toList();
            Users.DoctorList doctorList = Users.DoctorList.newBuilder()
                .addAllDoctors(grpcDoctorsList)
                .build();

            responseObserver.onNext(doctorList);
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void updateDoctor(Users.UpdateDoctorRequest request, StreamObserver<Users.Doctor> responseObserver) {
        this.logger.info("Called RPC Update Doctor");

        try {
            Doctor updatedDoctor = doctorRepository.update(request);
            responseObserver.onNext(grpcDoctorFromDomainModel(updatedDoctor));
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteDoctorById(Int32Value request, StreamObserver<Empty> responseObserver) {
        this.logger.info("Called RPC Delete Doctor");
        int id = request.getValue();

        try {
            doctorRepository.delete(id);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void countOfPatientsForDoctors(Empty request, StreamObserver<Users.CountOfPatientsForDoctorsResponse> responseObserver) {
        this.logger.info("Called RPC Count of Patients For All GP Doctors");

        try {
            List<PatientCountDTO> patientCountDTOList = doctorRepository.countOfPatientsForDoctors();
            List<Users.PatientCount> patientCountList = patientCountDTOList.stream()
                .map(DoctorService::grpcPatientCountFromDomainModel)
                .toList();

            Users.CountOfPatientsForDoctorsResponse list = Users.CountOfPatientsForDoctorsResponse.newBuilder()
                .addAllPatientCountList(patientCountList)
                .build();

            responseObserver.onNext(list);
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    static Users.Doctor grpcDoctorFromDomainModel(Doctor doctor) {
        Users.Specialty grpcSpecialty = Users.Specialty.newBuilder()
            .setId(doctor.getSpecialty().getId())
            .setSpecialtyName(doctor.getSpecialty().getSpecialtyName())
            .setSpecialtyDescription(doctor.getSpecialty().getSpecialtyDescription())
            .build();

        return Users.Doctor.newBuilder()
            .setId(doctor.getId())
            .setFirstName(doctor.getFirstName())
            .setLastName(doctor.getLastName())
            .setEmail(doctor.getEmail())
            .setPassword(doctor.getPassword())
            .setSpecialty(grpcSpecialty)
            .setIsGp(doctor.isGp())
            .build();
    }

    static Users.PatientCount grpcPatientCountFromDomainModel(PatientCountDTO patientCountDTO) {
        return Users.PatientCount.newBuilder()
            .setDoctorId(patientCountDTO.getDoctorId())
            .setDoctorFirstName(patientCountDTO.getDoctorFirstName())
            .setDoctorLastName(patientCountDTO.getDoctorLastName())
            .setPatientCount(Math.toIntExact(patientCountDTO.getPatientCount()))
            .build();
    }
}
