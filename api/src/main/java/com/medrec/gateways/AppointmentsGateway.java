package com.medrec.gateways;

import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
import com.google.protobuf.StringValue;
import com.medrec.exception_handling.ExceptionsMapper;
import com.medrec.grpc.appointments.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class AppointmentsGateway {
    private final Logger logger = Logger.getLogger(AppointmentsGateway.class.getName());
    private final ManagedChannel channel;

    private final AppointmentsServiceGrpc.AppointmentsServiceBlockingStub appointmentsService;
    private final DiagnosesServiceGrpc.DiagnosesServiceBlockingStub diagnosesService;
    private final IcdServiceGrpc.IcdServiceBlockingStub icdService;
    private final SickLeaveServiceGrpc.SickLeaveServiceBlockingStub sickLeaveService;

    public AppointmentsGateway() {
        int port = Integer.parseInt(System.getenv("APPOINTMENTS_PORT"));
        String host = System.getenv("APPOINTMENTS_HOST");

        try {
            this.logger.info("Initializing Appointments Gateway with host: " + host + " and port: " + port);
            channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        } catch (Exception e) {
            this.logger.severe("Could not connect to Users Service");
            throw e;
        }

        appointmentsService = AppointmentsServiceGrpc.newBlockingStub(channel);
        diagnosesService = DiagnosesServiceGrpc.newBlockingStub(channel);
        icdService = IcdServiceGrpc.newBlockingStub(channel);
        sickLeaveService = SickLeaveServiceGrpc.newBlockingStub(channel);
    }

    public Appointments.Appointment createAppointment(Appointments.CreateAppointmentRequest request) throws RuntimeException {
        try {
            return appointmentsService.createAppointment(request);
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Appointments.Appointment getAppointmentById(int id) throws RuntimeException {
        try {
            return appointmentsService.getAppointmentById(Int32Value.of(id));
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Appointments.AppointmentsList getAllAppointments() {
        try {
            return appointmentsService.getAllAppointments(Empty.getDefaultInstance());
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Appointments.AppointmentsList getAllByPatientEmail(String email) {
        try {
            return appointmentsService.getAllByPatientEmail(StringValue.of(email));
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Appointments.AppointmentsList getAllByPatientId(int id) {
        try {
            return appointmentsService.getAllByPatientId(Int32Value.of(id));
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Appointments.AppointmentsList getAllByDoctorEmail(String email) {
        try {
            return appointmentsService.getAllByDoctorEmail(StringValue.of(email));
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Appointments.AppointmentsList getAllByDoctorId(int id) {
        try {
            return appointmentsService.getAllByDoctorId(Int32Value.of(id));
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Appointments.Appointment updateAppointment(Appointments.UpdateAppointmentRequest request) throws RuntimeException {
        try {
            return appointmentsService.updateAppointment(request);
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public void deleteAppointment(int id) throws RuntimeException {
        try {
            Empty result = appointmentsService.deleteAppointmentById(Int32Value.of(id));
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public void cascadeDeletePatientAppointments(int patientId) throws RuntimeException {
        try {
            Empty result = appointmentsService.cascadeDeletePatientAppointments(Int32Value.of(patientId));
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public void cascadeDeleteDoctorAppointments(int doctorId) throws RuntimeException {
        try {
            Empty result = appointmentsService.cascadeDeleteDoctorAppointments(Int32Value.of(doctorId));
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Appointments.Diagnosis createDiagnosis(Appointments.CreateDiagnosisRequest request) throws RuntimeException {
        try {
            return diagnosesService.createDiagnosis(request);
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Appointments.Diagnosis getDiagnosisById(int id) throws RuntimeException {
        try {
            return diagnosesService.getDiagnosisById(Int32Value.of(id));
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Appointments.DiagnosesList getAllDiagnoses() {
        try {
            return diagnosesService.getAllDiagnoses(Empty.getDefaultInstance());
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Appointments.Diagnosis updateDiagnosis(Appointments.UpdateDiagnosisRequest request) throws RuntimeException {
        try {
            return diagnosesService.updateDiagnosis(request);
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public void deleteDiagnosis(int id) throws RuntimeException {
        try {
            Empty result = diagnosesService.deleteDiagnosisById(Int32Value.of(id));
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Appointments.Icd createIcd(Appointments.CreateIcdRequest request) throws RuntimeException {
        try {
            return icdService.createIcd(request);
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Appointments.Icd getIcdById(int id) throws RuntimeException {
        try {
            return icdService.getIcdById(Int32Value.of(id));
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Appointments.IcdEntitiesList getAllIcdEntities() {
        try {
            return icdService.getAllIcdEntities(Empty.getDefaultInstance());
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Appointments.Icd updateIcd(Appointments.UpdateIcdRequest request) throws RuntimeException {
        try {
            return icdService.updateIcd(request);
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public void deleteIcd(int id) throws RuntimeException {
        try {
            Empty result = icdService.deleteIcdById(Int32Value.of(id));
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Appointments.SickLeave createSickLeave(Appointments.CreateSickLeaveRequest request) throws RuntimeException {
        try {
            return sickLeaveService.createSickLeave(request);
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Appointments.SickLeave getSickLeaveById(int id) throws RuntimeException {
        try {
            return sickLeaveService.getSickLeaveById(Int32Value.of(id));
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Appointments.SickLeaveEntitiesList getAllSickLeaveEntities() {
        try {
            return sickLeaveService.getAllSickLeaveEntities(Empty.getDefaultInstance());
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Appointments.SickLeave updateSickLeave(Appointments.UpdateSickLeaveRequest request) {
        try {
            return sickLeaveService.updateSickLeave(request);
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public void deleteSickLeave(int id) throws RuntimeException {
        try {
            Empty result = sickLeaveService.deleteSickLeaveById(Int32Value.of(id));
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Appointments.IcdEntitiesList startAppointmentFetchIcds(Appointments.StartAppointmentFetchIcdsRequest request) throws RuntimeException {
        try {
            return appointmentsService.startAppointmentFetchIcds(request);
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public void finishAppointmentAddDiagnosis(Appointments.FinishAppointmentAddDiagnosisRequest request) throws RuntimeException {
        try {
            Empty result = appointmentsService.finishAppointmentAddDiagnosis(request);
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Appointments.IdsList getAllPatientIdsForIcd(Int32Value icdId) throws RuntimeException {
        try {
            return appointmentsService.getAllPatientIdsForIcd(icdId);
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Appointments.IcdOccurrenceList mostFrequentIcds(Int32Value limit) throws RuntimeException {
        try {
            return icdService.mostFrequentIcds(limit);
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Appointments.DoctorAppointmentsCountList getDoctorAppointmentsCount() throws RuntimeException {
        try {
            return appointmentsService.getAppointmentsCountForDoctors(Empty.getDefaultInstance());
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Appointments.AppointmentsByPatientList getAppointmentsByPatientList() throws RuntimeException {
        try {
            return appointmentsService.listAppointmentsByPatients(Empty.getDefaultInstance());
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Appointments.AppointmentsList getAppointmentsForTimePeriod(Appointments.ListAppointmentsForTimePeriodRequest request) throws RuntimeException {
        try {
            return appointmentsService.listAppointmentsForTimePeriod(request);
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Appointments.MonthWithMostSickLeaves getMonthWithMostSickLeaves(Appointments.MostSickLeavesRequest request) throws RuntimeException {
        try {
            return appointmentsService.getMonthWithMostSickLeaves(request);
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }

    public Appointments.DoctorsWithMostSickLeavesList getDoctorsBySickLeaveCount(Appointments.DoctorsBySickLeaveCountRequest request) throws RuntimeException {
        try {
            return this.appointmentsService.getDoctorsBySickLeaveCount(request);
        } catch (StatusRuntimeException e) {
            throw ExceptionsMapper.translateStatusRuntimeException(e);
        }
    }
}
