package com.medrec.services;

import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
import com.medrec.exception_handling.ExceptionsMapper;
import com.medrec.grpc.appointments.Appointments;
import com.medrec.grpc.appointments.DiagnosesServiceGrpc;
import com.medrec.persistence.diagnosis.Diagnosis;
import com.medrec.persistence.diagnosis.DiagnosisRepository;
import com.medrec.persistence.leave.SickLeave;
import com.medrec.utils.Utils;
import io.grpc.stub.StreamObserver;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class DiagnosesService extends DiagnosesServiceGrpc.DiagnosesServiceImplBase {
    public static DiagnosesService instance;

    private final DiagnosisRepository diagnosisRepository = DiagnosisRepository.getInstance();

    private final Logger logger = Logger.getLogger(DiagnosesService.class.getName());

    private DiagnosesService() {}

    public static DiagnosesService getInstance() {
        if (instance == null) {
            instance = new DiagnosesService();
        }
        return instance;
    }

    @Override
    public void createDiagnosis(Appointments.CreateDiagnosisRequest request, StreamObserver<Appointments.Diagnosis> responseObserver) {
        this.logger.info("Called RPC Create Diagnosis");

        try {
            Diagnosis diagnosis;
            if (request.hasSickLeaveDate() && request.hasSickLeaveDays()) {
                LocalDate date = Utils.parseDate(request.getSickLeaveDate());
                SickLeave sickLeave = new SickLeave(
                    date,
                    request.getSickLeaveDays()
                );

                diagnosis = this.diagnosisRepository.save(
                    request.getTreatmentDescription(),
                    request.getIcdId(),
                    Optional.of(sickLeave)
                );
            } else {
                diagnosis = this.diagnosisRepository.save(
                    request.getTreatmentDescription(),
                    request.getIcdId(),
                    Optional.empty()
                );
            }

            responseObserver.onNext(Utils.getDiagnosisFromDomainModel(diagnosis));
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void getDiagnosisById(Int32Value request, StreamObserver<Appointments.Diagnosis> responseObserver) {
        int id = request.getValue();
        this.logger.info("Called RPC Get Diagnosis By Id: " + id);

        try {
            Diagnosis diagnosis = this.diagnosisRepository.getById(id);
            responseObserver.onNext(Utils.getDiagnosisFromDomainModel(diagnosis));
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void getAllDiagnoses(Empty request, StreamObserver<Appointments.DiagnosesList> responseObserver) {
        this.logger.info("Called RPC Get All Diagnoses");

        try {
            List<Diagnosis> diagnoses = this.diagnosisRepository.findAll();
            List<Appointments.Diagnosis> grpcDiagnoses = diagnoses.stream()
                .map(Utils::getDiagnosisFromDomainModel)
                .toList();

            Appointments.DiagnosesList list = Appointments.DiagnosesList.newBuilder()
                .addAllDiagnoses(grpcDiagnoses)
                .build();

            responseObserver.onNext(list);
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void updateDiagnosis(Appointments.UpdateDiagnosisRequest request, StreamObserver<Appointments.Diagnosis> responseObserver) {
        int id = request.getId();
        this.logger.info("Called RPC Update Diagnosis for id (" + id + ")");

        SickLeave sickLeave = null;
        if (request.hasSickLeaveDate() && request.hasSickLeaveDays()) {
            LocalDate date = Utils.parseDate(request.getSickLeaveDate());
            sickLeave = new SickLeave(
                date,
                request.getSickLeaveDays()
            );
        }

        try {
            Diagnosis diagnosis = this.diagnosisRepository.update(
                request.getId(),
                request.hasTreatmentDescription() ? Optional.of(request.getTreatmentDescription()) : Optional.empty(),
                request.hasIcdId() ? Optional.of(request.getIcdId()) : Optional.empty() ,
                Optional.ofNullable(sickLeave)
            );

            responseObserver.onNext(Utils.getDiagnosisFromDomainModel(diagnosis));
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteDiagnosisById(Int32Value request, StreamObserver<Empty> responseObserver) {
        int id = request.getValue();
        this.logger.info("Called RPC Delete Diagnosis By Id: " + id);

        try {
            this.diagnosisRepository.delete(id);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }
}
