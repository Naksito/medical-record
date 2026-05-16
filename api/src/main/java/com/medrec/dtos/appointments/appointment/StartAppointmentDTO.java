package com.medrec.dtos.appointments.appointment;

public class StartAppointmentDTO {
    Integer appointmentId;
    Integer doctorId;

    public StartAppointmentDTO() {
    }

    public StartAppointmentDTO(Integer appointmentId, Integer doctorId) {
        this.appointmentId = appointmentId;
        this.doctorId = doctorId;
    }

    public Integer getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }
}
