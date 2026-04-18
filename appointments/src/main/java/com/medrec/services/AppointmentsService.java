package com.medrec.services;

import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
import com.google.protobuf.StringValue;
import com.medrec.dtos.appointment.DoctorAppointmentsCountDTO;
import com.medrec.dtos.appointment.DoctorSickLeaveCountDTO;
import com.medrec.dtos.appointment.MonthWithMostSickLeavesDTO;
import com.medrec.exception_handling.ExceptionsMapper;
import com.medrec.grpc.appointments.Appointments;
import com.medrec.grpc.appointments.AppointmentsServiceGrpc;
import com.medrec.persistence.appointment.Appointment;
import com.medrec.persistence.appointment.AppointmentsRepository;
import com.medrec.persistence.icd.Icd;
import com.medrec.persistence.icd.IcdRepository;
import com.medrec.utils.CascadeEntityType;
import com.medrec.utils.Utils;
import io.grpc.stub.StreamObserver;

import java.util.*;
import java.util.logging.Logger;


public class AppointmentsService extends AppointmentsServiceGrpc.AppointmentsServiceImplBase {
    private static AppointmentsService instance;

    private final Logger logger = Logger.getLogger(AppointmentsService.class.getName());

    private final AppointmentsRepository appointmentsRepository = AppointmentsRepository.getInstance();
    private final IcdRepository icdRepository = IcdRepository.getInstance();

    private AppointmentsService() {}

    public static AppointmentsService getInstance() {
        if (instance == null) {
            instance = new AppointmentsService();
        }
        return instance;
    }

    @Override
    public void createAppointment(Appointments.CreateAppointmentRequest request, StreamObserver<Appointments.Appointment> responseObserver) {
        this.logger.info("Called RPC Create Appointment");

        try {
            Appointment appointment = this.appointmentsRepository.save(
                request.getDate(),
                request.getTime(),
                request.getDoctorId(),
                request.getPatientId()
            );

            responseObserver.onNext(Utils.getAppointmentFromDomainModel(appointment));
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void getAppointmentById(Int32Value request, StreamObserver<Appointments.Appointment> responseObserver) {
        int id = request.getValue();
        this.logger.info("Called RPC Get Appointment By Id: " + id);

        try {
            Appointment appointment = this.appointmentsRepository.getById(id);
            responseObserver.onNext(Utils.getAppointmentFromDomainModel(appointment));
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void getAllAppointments(Empty request, StreamObserver<Appointments.AppointmentsList> responseObserver) {
        this.logger.info("Called RPC Get All Appointments");

        try {
            List<Appointment> appointments = this.appointmentsRepository.findAll();
            List<Appointments.Appointment> grpcAppointments = appointments.stream()
                .map(Utils::getAppointmentFromDomainModel)
                .toList();

            Appointments.AppointmentsList list = Appointments.AppointmentsList.newBuilder()
                .addAllAppointments(grpcAppointments)
                .build();

            responseObserver.onNext(list);
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void getAllByPatientEmail(StringValue request, StreamObserver<Appointments.AppointmentsList> responseObserver) {
        String email = request.getValue();
        this.logger.info("Called RPC Get All Appointments By Patient Email: " + email);

        try {
            List<Appointment> appointments = this.appointmentsRepository.findAllByPatientEmail(email);
            List<Appointments.Appointment> grpcAppointments = appointments.stream()
                .map(Utils::getAppointmentFromDomainModel)
                .toList();

            Appointments.AppointmentsList list = Appointments.AppointmentsList.newBuilder()
                .addAllAppointments(grpcAppointments)
                .build();

            responseObserver.onNext(list);
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void getAllByPatientId(Int32Value request, StreamObserver<Appointments.AppointmentsList> responseObserver) {
        int id = request.getValue();
        this.logger.info("Called RPC Get All Appointments By Patient Id " + id);

        try {
            List<Appointment> appointments = this.appointmentsRepository.findAllByPatientId(id);
            List<Appointments.Appointment> grpcAppointments = appointments.stream()
                .map(Utils::getAppointmentFromDomainModel)
                .toList();

            Appointments.AppointmentsList list = Appointments.AppointmentsList.newBuilder()
                .addAllAppointments(grpcAppointments)
                .build();
            responseObserver.onNext(list);
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void getAllByDoctorEmail(StringValue request, StreamObserver<Appointments.AppointmentsList> responseObserver) {
        String email = request.getValue();
        this.logger.info("Called RPC Get All Appointments By Doctor Email: " + email);

        try {
            List<Appointment> appointments = this.appointmentsRepository.findAllByDoctorEmail(email);
            List<Appointments.Appointment> grpcAppointments = appointments.stream()
                .map(Utils::getAppointmentFromDomainModel)
                .toList();

            Appointments.AppointmentsList list = Appointments.AppointmentsList.newBuilder()
                .addAllAppointments(grpcAppointments)
                .build();

            responseObserver.onNext(list);
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void getAllByDoctorId(Int32Value request, StreamObserver<Appointments.AppointmentsList> responseObserver) {
        int id = request.getValue();
        this.logger.info("Called RPC Get All Appointments By Doctor Id: " + id);

        try {
            List<Appointment> appointments = this.appointmentsRepository.findAllByDoctorId(id);
            List<Appointments.Appointment> grpcAppointments = appointments.stream()
                .map(Utils::getAppointmentFromDomainModel)
                .toList();

            Appointments.AppointmentsList list = Appointments.AppointmentsList.newBuilder()
                .addAllAppointments(grpcAppointments)
                .build();

            responseObserver.onNext(list);
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void updateAppointment(Appointments.UpdateAppointmentRequest request, StreamObserver<Appointments.Appointment> responseObserver) {
        int id = request.getId();
        this.logger.info("Called RPC Update Appointment for id: " + id);

        try {
            Appointment appointment = this.appointmentsRepository.update(
                id,
                request.hasStatus() ? Optional.of(request.getStatus()) : Optional.empty(),
                request.hasDiagnosisId() ? Optional.of(request.getDiagnosisId()) : Optional.empty()
            );
            responseObserver.onNext(Utils.getAppointmentFromDomainModel(appointment));
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteAppointmentById(Int32Value request, StreamObserver<Empty> responseObserver) {
        int id = request.getValue();
        this.logger.info("Called RPC Delete Appointment By Id: " + id);

        try {
            this.appointmentsRepository.delete(id);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void cascadeDeletePatientAppointments(Int32Value request, StreamObserver<Empty> responseObserver) {
        int id = request.getValue();
        this.logger.info("Cascade deleting all appointments for patient with id: " + id);

        try {
            this.appointmentsRepository.genericCascadeDelete(id, CascadeEntityType.PATIENT);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void cascadeDeleteDoctorAppointments(Int32Value request, StreamObserver<Empty> responseObserver) {
        int id = request.getValue();
        this.logger.info("Cascade deleting all appointments for doctor with id: " + id);

        try {
            this.appointmentsRepository.genericCascadeDelete(id, CascadeEntityType.DOCTOR);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void startAppointmentFetchIcds(Appointments.StartAppointmentFetchIcdsRequest request, StreamObserver<Appointments.IcdEntitiesList> responseObserver) {
        int appointmentId = request.getAppointmentId();
        int doctorId = request.getDoctorId();

        this.logger.info("Called RPC Start Appointment Fetch Icds for appointmentId: " + appointmentId + " doctorId: " + doctorId);

        try {
            List<Icd> icdEntities = this.appointmentsRepository.startAppointmentFetchIcds(appointmentId, doctorId);
            List<Appointments.Icd> grpcIcdEntities = icdEntities.stream()
                .map(Utils::getIcdFromDomainModel)
                .toList();

            Appointments.IcdEntitiesList list = Appointments.IcdEntitiesList.newBuilder()
                .addAllIcdEntities(grpcIcdEntities)
                .build();

            responseObserver.onNext(list);
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void getAllPatientIdsForIcd(Int32Value request, StreamObserver<Appointments.IdsList> responseObserver) {
        int appointmentId = request.getValue();

        try {
            List<Integer> patientIds = this.appointmentsRepository.getAllPatientIdsForIcd(appointmentId);
            Appointments.IdsList idsList  = Appointments.IdsList.newBuilder()
                .addAllId(patientIds)
                .build();

            responseObserver.onNext(idsList);
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void finishAppointmentAddDiagnosis(
        Appointments.FinishAppointmentAddDiagnosisRequest request,
        StreamObserver<Empty> responseObserver
    ) {
        this.logger.info("Called RPC Finish Appointment Add Diagnosis");

        try {
            Appointments.CreateDiagnosisRequest diagnosisRequest = request.getDiagnosis();
            this.appointmentsRepository.finishAppointmentAddDiagnosis(
                request.getAppointmentId(),
                diagnosisRequest.getTreatmentDescription(),
                diagnosisRequest.getIcdId(),
                diagnosisRequest.hasSickLeaveDate() ? Optional.of(diagnosisRequest.getSickLeaveDate()) : Optional.empty(),
                diagnosisRequest.hasSickLeaveDays() ? Optional.of(diagnosisRequest.getSickLeaveDays()) : Optional.empty()
            );

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void getAppointmentsCountForDoctors(
        Empty request,
        StreamObserver<Appointments.DoctorAppointmentsCountList> streamObserver
    ) {
        this.logger.info("Called RPC Get Appointments Count For Doctors");

        try {
            List<DoctorAppointmentsCountDTO> dtos = this.appointmentsRepository.getDoctorAppointmentsCount();
            List<Appointments.DoctorAppointmentsCount> grpcAppointmentsCount = dtos.stream()
                .map(Utils::getDoctorAppointmentsCountFromDto)
                .toList();

            Appointments.DoctorAppointmentsCountList list = Appointments.DoctorAppointmentsCountList.newBuilder()
                .addAllCount(grpcAppointmentsCount)
                .build();

            streamObserver.onNext(list);
            streamObserver.onCompleted();
        } catch (RuntimeException e) {
            streamObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void listAppointmentsByPatients(Empty request, StreamObserver<Appointments.AppointmentsByPatientList> streamObserver) {
        this.logger.info("Called RPC List Appointments By Patients");

        try {
            List<Appointment> appointments = this.appointmentsRepository.findAll();
            Map<Integer, List<Appointment>> appointmentsByPatientMap = Utils.mapAppointmentsByPatient(appointments);
            List<Appointments.AppointmentsByPatient> appointmentsByPatientsList = Utils.getGrpcAppointmentsByPatientList(appointmentsByPatientMap);

            Appointments.AppointmentsByPatientList list = Appointments.AppointmentsByPatientList.newBuilder()
                .addAllList(appointmentsByPatientsList)
                .build();

            streamObserver.onNext(list);
            streamObserver.onCompleted();
        } catch (RuntimeException e) {
            streamObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void listAppointmentsForTimePeriod(
        Appointments.ListAppointmentsForTimePeriodRequest request,
        StreamObserver<Appointments.AppointmentsList> streamObserver
    ) {
       this.logger.info("Called RPC List Appointments For Time Period");

       try {
           List<Appointment> appointments = this.appointmentsRepository.getAppointmentsForTimePeriod(
               request.getStartDate(),
               request.getEndDate(),
               request.hasDoctorId() ? Optional.of(request.getDoctorId()) : Optional.empty()
           );

           List<Appointments.Appointment> list = appointments.stream()
               .map(Utils::getAppointmentFromDomainModel)
               .toList();

           Appointments.AppointmentsList response = Appointments.AppointmentsList.newBuilder()
               .addAllAppointments(list)
               .build();

           streamObserver.onNext(response);
           streamObserver.onCompleted();
       } catch (RuntimeException e) {
           streamObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
       }
    }

    @Override
    public void getMonthWithMostSickLeaves(
        Appointments.MostSickLeavesRequest request,
        StreamObserver<Appointments.MonthWithMostSickLeaves> streamObserver
    ) {
        this.logger.info("Called RPC Get Month With Most Sick Leaves");

        try {
            String year = request.getYear();
            MonthWithMostSickLeavesDTO dto = this.appointmentsRepository.getMonthWithMostSickLeaves(year);

            streamObserver.onNext(Utils.getGrpcMonthWithMostSickLeaves(dto));
            streamObserver.onCompleted();
        } catch (RuntimeException e) {
            streamObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void getDoctorsBySickLeaveCount(
        Appointments.DoctorsBySickLeaveCountRequest request,
        StreamObserver<Appointments.DoctorsWithMostSickLeavesList> streamObserver
    ) {
        this.logger.info("Called RPC Get Doctors By Sick Leave Count");

        try {
            int limit = request.getLimit();

            List<DoctorSickLeaveCountDTO> dtos = this.appointmentsRepository.getDoctorsBySickLeaveCount(limit);

            List<Appointments.DoctorSickLeaveCount> grpcList = dtos.stream()
                .map(Utils::getGrpcDoctorSickLeaveCount)
                .toList();

            Appointments.DoctorsWithMostSickLeavesList response = Appointments.DoctorsWithMostSickLeavesList.newBuilder()
                .addAllDoctorWithMostSickLeaves(grpcList)
                .build();

            streamObserver.onNext(response);
            streamObserver.onCompleted();
        } catch (RuntimeException e) {
            streamObserver.onError(ExceptionsMapper.toStatusRuntimeException(e));
        }
    }
}
