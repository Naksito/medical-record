package com.medrec.utils;

import com.medrec.dtos.appointments.appointment.*;
import com.medrec.dtos.appointments.diagnosis.DiagnosisDTO;
import com.medrec.dtos.appointments.icd.IcdDTO;
import com.medrec.dtos.appointments.icd.IcdOccurrenceDTO;
import com.medrec.dtos.appointments.sick_leave.SickLeaveDTO;
import com.medrec.grpc.appointments.Appointments;

import java.util.List;
import java.util.Optional;

public class Utils {
    public static AppointmentDTO getDTOFromAppointmentsGrpc(Appointments.Appointment appointment) {
        DiagnosisDTO diagnosisDto = null;

        if (appointment.hasDiagnosis()) {
            diagnosisDto = getDtoFromDiagnosisGrpc(appointment.getDiagnosis());
        }

        return new AppointmentDTO(
            appointment.getId(),
            appointment.getDate(),
            appointment.getTime(),
            appointment.getDoctorId(),
            appointment.getPatientId(),
            appointment.getStatus(),
            Optional.ofNullable(diagnosisDto)
        );
    }

    public static DiagnosisDTO getDtoFromDiagnosisGrpc(Appointments.Diagnosis diagnosis) {
        SickLeaveDTO sickLeaveDto = null;

        if (diagnosis.hasSickLeave()) {
            sickLeaveDto = getDtoFromSickLeaveGrpc(diagnosis.getSickLeave());
        }

        return new DiagnosisDTO(
            diagnosis.getId(),
            diagnosis.getTreatmentDescription(),
            getDtoFromIcdGrpc(diagnosis.getIcd()),
            Optional.ofNullable(sickLeaveDto)
        );
    }

    public static IcdDTO getDtoFromIcdGrpc(Appointments.Icd icd) {
        return new IcdDTO(
            icd.getId(),
            icd.getCode(),
            icd.getDescription()
        );
    }

    public static SickLeaveDTO getDtoFromSickLeaveGrpc(Appointments.SickLeave sickLeave) {
        return new SickLeaveDTO(
            sickLeave.getId(),
            sickLeave.getDate(),
            sickLeave.getDaysOfLeave()
        );
    }

    public static IcdOccurrenceDTO getOccurrenceDtoFromIcdGrpc(Appointments.IcdOccurrence icdOccurrence) {
        return new IcdOccurrenceDTO(
            icdOccurrence.getIcdId(),
            icdOccurrence.getIcdCode(),
            icdOccurrence.getOccurrence()
        );
    }

    public static DoctorAppointmentsCountDTO getDoctorAppointmentsCountFromGrpc(
        Appointments.DoctorAppointmentsCount doctorAppointmentsCount
    ) {
        return new DoctorAppointmentsCountDTO(
            doctorAppointmentsCount.getDoctorId(),
            doctorAppointmentsCount.getAppointmentsCount()
        );
    }

    public static AppointmentsByPatientDTO getAppointmentsByPatientFromGrpc(Appointments.AppointmentsByPatient appointmentsByPatient) {
        Appointments.AppointmentsList appointmentsList = appointmentsByPatient.getAppointments();
        Integer patientId = appointmentsByPatient.getPatientId();

        List<AppointmentDTO> appointmentDTOs = appointmentsList.getAppointmentsList().stream()
            .map(Utils::getDTOFromAppointmentsGrpc)
            .toList();

        return new AppointmentsByPatientDTO(patientId, appointmentDTOs);
    }

    public static MonthWithMostSickLeavesDTO getMonthWithMostSickLeavesFromGrpc(Appointments.MonthWithMostSickLeaves monthWithMostSickLeaves) {
        return new MonthWithMostSickLeavesDTO(
            monthWithMostSickLeaves.getDate(),
            monthWithMostSickLeaves.getCount()
        );
    }

    public static DoctorSickLeaveCountDTO getDoctorsBySickLeaveCount(Appointments.DoctorSickLeaveCount doctorSickLeaveCount) {
        return new DoctorSickLeaveCountDTO(
            doctorSickLeaveCount.getDoctorId(),
            doctorSickLeaveCount.getCount()
        );
    }
}
