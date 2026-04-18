package com.medrec.services;

import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
import com.medrec.dtos.CreateSpecialtyDTO;
import com.medrec.exception_handling.ExceptionsMapper;
import com.medrec.grpc.users.SpecialtyServiceGrpc;
import com.medrec.grpc.users.Users;
import com.medrec.persistence.specialty.Specialty;
import com.medrec.persistence.specialty.SpecialtyRepository;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.logging.Logger;

public class SpecialtyService extends SpecialtyServiceGrpc.SpecialtyServiceImplBase {
    private static SpecialtyService instance;

    private final Logger logger = Logger.getLogger(SpecialtyService.class.getName());

    private final SpecialtyRepository specialtyRepository = SpecialtyRepository.getInstance();

    private SpecialtyService() {
    }

    public static SpecialtyService getInstance() {
        if (instance == null) {
            instance = new SpecialtyService();
        }
        return instance;
    }

    @Override
    public void createSpecialty(Users.CreateSpecialtyRequest request, StreamObserver<Users.Specialty> responseObserver) {
        this.logger.info("Called RPC Create Specialty");

        try {
            CreateSpecialtyDTO dto = new CreateSpecialtyDTO(
                request.getSpecialtyName(),
                request.getSpecialtyDescription()
            );

            Specialty specialty = specialtyRepository.save(dto);
            responseObserver.onNext(grpcFromDomainModel(specialty));
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void getSpecialtyById(Int32Value request, StreamObserver<Users.Specialty> responseObserver) {
        this.logger.info("Called RPC Get Specialty By Id");
        int id = request.getValue();

        try {
            Specialty specialty = specialtyRepository.findById(id);
            responseObserver.onNext(grpcFromDomainModel(specialty));
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void getAllSpecialties(Empty request, StreamObserver<Users.SpecialtiesList> responseObserver) {
        this.logger.info("Called RPC Get All Specialties");
        try {
            List<Specialty> specialties = specialtyRepository.findAll();
            List<Users.Specialty> grpcSpecialtiesList = specialties.stream()
                .map(SpecialtyService::grpcFromDomainModel)
                .toList();
            Users.SpecialtiesList specialtiesList = Users.SpecialtiesList.newBuilder()
                .addAllSpecialties(grpcSpecialtiesList)
                .build();

            this.logger.info(String.format("Found %s specialties", specialties.size()));
            responseObserver.onNext(specialtiesList);
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void updateSpecialty(Users.UpdateSpecialtyRequest request, StreamObserver<Users.Specialty> responseObserver) {
        this.logger.info("Called RPC Update Specialty");

        try {

            Specialty specialty = specialtyRepository.update(request);
            responseObserver.onNext(grpcFromDomainModel(specialty));
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteSpecialtyById(Int32Value request, StreamObserver<Empty> responseObserver) {
        this.logger.info("Called RPC Delete Specialty");
        int id = request.getValue();

        try {
            specialtyRepository.delete(id);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    private static Users.Specialty grpcFromDomainModel(Specialty specialty) {
        return Users.Specialty.newBuilder()
            .setId(specialty.getId())
            .setSpecialtyName(specialty.getSpecialtyName())
            .setSpecialtyDescription(specialty.getSpecialtyDescription())
            .build();
    }
}
