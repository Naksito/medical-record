package com.medrec.dtos.appointments.appointment;

import java.util.List;

public class AppointmentsByPatientDTO {
    private Integer patientId;
    private List<AppointmentDTO> appointments;

    public AppointmentsByPatientDTO() {
    }

    public AppointmentsByPatientDTO(Integer patientId, List<AppointmentDTO> appointments) {
        this.patientId = patientId;
        this.appointments = appointments;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public List<AppointmentDTO> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<AppointmentDTO> appointments) {
        this.appointments = appointments;
    }
}
