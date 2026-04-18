package com.medrec.services;

import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
import com.medrec.exception_handling.ExceptionsMapper;
import com.medrec.grpc.appointments.Appointments;
import com.medrec.grpc.appointments.SickLeaveServiceGrpc;
import com.medrec.persistence.leave.SickLeave;
import com.medrec.persistence.leave.SickLeaveRepository;
import com.medrec.utils.Utils;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class SickLeaveService extends SickLeaveServiceGrpc.SickLeaveServiceImplBase {
    private static SickLeaveService instance;

    private final Logger logger = Logger.getLogger(SickLeaveService.class.getName());

    private final SickLeaveRepository sickLeaveRepository = SickLeaveRepository.getInstance();

    private SickLeaveService() {}

    public static SickLeaveService getInstance() {
        if (instance == null) {
            instance = new SickLeaveService();
        }
        return instance;
    }

    @Override
    public void createSickLeave(Appointments.CreateSickLeaveRequest request, StreamObserver<Appointments.SickLeave> responseObserver) {
        this.logger.info("Called RPC Create Sick Leave");

        try {
            SickLeave sickLeave = this.sickLeaveRepository.save(
                request.getDate(),
                request.getDaysOfLeave()
            );

            responseObserver.onNext(Utils.getSickLeaveFromDomainModel(sickLeave));
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void getSickLeaveById(Int32Value request, StreamObserver<Appointments.SickLeave> responseObserver) {
        int id = request.getValue();
        this.logger.info("Called RPC Get Sick Leave By Id: " + id);

        try {
            SickLeave sickLeave = this.sickLeaveRepository.getById(id);
            responseObserver.onNext(Utils.getSickLeaveFromDomainModel(sickLeave));
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void getAllSickLeaveEntities(Empty request, StreamObserver<Appointments.SickLeaveEntitiesList> responseObserver) {
        this.logger.info("Called RPC Get All Sick Leave Entities");

        try {
            List<SickLeave> sickLeaves = this.sickLeaveRepository.findAll();
            List<Appointments.SickLeave> grpcSickLeaves = sickLeaves.stream()
                .map(Utils::getSickLeaveFromDomainModel)
                .toList();

            Appointments.SickLeaveEntitiesList list = Appointments.SickLeaveEntitiesList.newBuilder()
                .addAllSickLeaveEntities(grpcSickLeaves)
                .build();

            responseObserver.onNext(list);
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void updateSickLeave(Appointments.UpdateSickLeaveRequest request, StreamObserver<Appointments.SickLeave> responseObserver) {
        int id = request.getId();
        this.logger.info("Called RPC Update Sick Leave for Id: " + id);

        try {
            SickLeave sickLeave = this.sickLeaveRepository.update(
                id,
                request.hasDate() ? Optional.of(request.getDate()) : Optional.empty(),
                request.hasDaysOfLeave() ? Optional.of(request.getDaysOfLeave()) : Optional.empty()
            );
            responseObserver.onNext(Utils.getSickLeaveFromDomainModel(sickLeave));
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteSickLeaveById(Int32Value request, StreamObserver<Empty> responseObserver) {
        int id = request.getValue();
        this.logger.info("Called RPC Delete Sick Leave By Id: " + id);

        try {
            this.sickLeaveRepository.delete(id);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }
}
