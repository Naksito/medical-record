package com.medrec.dtos.appointments.appointment;

public class DoctorAppointmentsCountDTO {
    private Integer doctorId;
    private Integer appointmentCount;

    public DoctorAppointmentsCountDTO() {
    }

    public DoctorAppointmentsCountDTO(Integer doctorId, Integer appointmentCount) {
        this.doctorId = doctorId;
        this.appointmentCount = appointmentCount;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public Integer getAppointmentCount() {
        return appointmentCount;
    }

    public void setAppointmentCount(Integer appointmentCount) {
        this.appointmentCount = appointmentCount;
    }
}
