package com.medrec.utils;

import com.medrec.dtos.appointment.DoctorAppointmentsCountDTO;
import com.medrec.dtos.appointment.DoctorSickLeaveCountDTO;
import com.medrec.dtos.appointment.MonthWithMostSickLeavesDTO;
import com.medrec.dtos.icd.IcdOccurrenceDTO;
import com.medrec.exception_handling.exceptions.BadRequestException;
import com.medrec.grpc.appointments.Appointments;
import com.medrec.persistence.appointment.Appointment;
import com.medrec.persistence.diagnosis.Diagnosis;
import com.medrec.persistence.icd.Icd;
import com.medrec.persistence.leave.SickLeave;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Utils {
    static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static LocalDate parseDate(String date) throws RuntimeException {
        try {
            return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new BadRequestException("date_time_not_parsed");
        }
    }

    public static Appointments.Diagnosis getDiagnosisFromDomainModel(Diagnosis diagnosis) {
        Appointments.Icd icd = getIcdFromDomainModel(diagnosis.getIcd());

        Appointments.Diagnosis.Builder builder = Appointments.Diagnosis.newBuilder();

        builder.setId(diagnosis.getId());
        builder.setTreatmentDescription(diagnosis.getTreatmentDescription());
        builder.setIcd(icd);

        if (diagnosis.getSickLeave() != null) {
            builder.setSickLeave(getSickLeaveFromDomainModel(diagnosis.getSickLeave()));
        }

        return builder.build();
    }

    public static Appointments.SickLeave getSickLeaveFromDomainModel(SickLeave sickLeave) {
        return Appointments.SickLeave.newBuilder()
            .setId(sickLeave.getId())
            .setDate(sickLeave.getStartDate().toString())
            .setDaysOfLeave(sickLeave.getDaysOfLeave())
            .build();
    }

    public static Appointments.Icd getIcdFromDomainModel(Icd icd) {
        return Appointments.Icd.newBuilder()
            .setId(icd.getId())
            .setCode(icd.getCode())
            .setDescription(icd.getDescription())
            .build();
    }

    public static Appointments.Appointment getAppointmentFromDomainModel(Appointment appointment) {
        LocalDateTime dateTime = appointment.getDateTime();

        String date = dateTime.format(dateFormatter);
        String time = dateTime.format(timeFormatter);

        Appointments.Appointment.Builder builder = Appointments.Appointment.newBuilder();
        builder.setId(appointment.getId());
        builder.setDate(date);
        builder.setTime(time);
        builder.setStatus(appointment.getStatus());
        builder.setDoctorId(appointment.getDoctorId());
        builder.setPatientId(appointment.getPatientId());

        if (appointment.getDiagnosis() != null) {
            builder.setDiagnosis(getDiagnosisFromDomainModel(appointment.getDiagnosis()));
        }

        return builder.build();
    }

    public static Appointments.IcdOccurrence getIcdOccurrenceFromDto(IcdOccurrenceDTO dto) {
        return Appointments.IcdOccurrence.newBuilder()
            .setIcdId(dto.getIcdId())
            .setIcdCode(dto.getIcdCode())
            .setOccurrence(dto.getOccurrence())
            .build();
    }

    public static Appointments.DoctorAppointmentsCount getDoctorAppointmentsCountFromDto(DoctorAppointmentsCountDTO dto) {
        return Appointments.DoctorAppointmentsCount.newBuilder()
            .setDoctorId(dto.getDoctorId())
            .setAppointmentsCount(dto.getAppointmentsCount())
            .build();
    }

    public static Map<Integer, List<Appointment>> mapAppointmentsByPatient(List<Appointment> appointments) {
        Map<Integer, List<Appointment>> appointmentsByPatientId = new HashMap<>();

        for (Appointment appointment : appointments) {
            appointmentsByPatientId.put(
                appointment.getPatientId(),
                getAppointmentsForPatientId(appointment.getPatientId(), appointments)
            );
        }

        return appointmentsByPatientId;
    }

    private static List<Appointment> getAppointmentsForPatientId(int patientId, List<Appointment> appointments) {
        List<Appointment> appointmentsForPatientId = new ArrayList<>();

        for (Appointment appointment : appointments) {
            if (appointment.getPatientId() == patientId) {
                appointmentsForPatientId.add(appointment);
            }
        }

        return appointmentsForPatientId;
    }

    public static List<Appointments.AppointmentsByPatient> getGrpcAppointmentsByPatientList(Map<Integer, List<Appointment>> appointmentsMap) {
        List<Appointments.AppointmentsByPatient> list = new ArrayList<>();

        for (Integer patientId : appointmentsMap.keySet()) {
            List<Appointments.Appointment> grpcAppointments = appointmentsMap.get(patientId).stream()
                .map(Utils::getAppointmentFromDomainModel)
                .toList();

            Appointments.AppointmentsList grpcAppointmentsList = Appointments.AppointmentsList.newBuilder()
                .addAllAppointments(grpcAppointments)
                .build();

            Appointments.AppointmentsByPatient grpcAppointmentsByPatient = Appointments.AppointmentsByPatient.newBuilder()
                .setPatientId(patientId)
                .setAppointments(grpcAppointmentsList)
                .build();

            list.add(grpcAppointmentsByPatient);
        }

        return list;
    }

    public static Appointments.MonthWithMostSickLeaves getGrpcMonthWithMostSickLeaves(MonthWithMostSickLeavesDTO dto) {
        return Appointments.MonthWithMostSickLeaves.newBuilder()
            .setDate(dto.getDate())
            .setCount(dto.getCount())
            .build();
    }

    public static Appointments.DoctorSickLeaveCount getGrpcDoctorSickLeaveCount(DoctorSickLeaveCountDTO dto) {
        return Appointments.DoctorSickLeaveCount.newBuilder()
            .setDoctorId(dto.getDoctorId())
            .setCount(dto.getCount())
            .build();
    }
}
