package com.medrec.dtos.appointment;

public class DoctorAppointmentsCountDTO {
    private Integer doctorId;
    private Integer appointmentsCount;

    public DoctorAppointmentsCountDTO() {
    }

    public DoctorAppointmentsCountDTO(Integer doctorId, Integer appointmentsCount) {
        this.doctorId = doctorId;
        this.appointmentsCount = appointmentsCount;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public Integer getAppointmentsCount() {
        return appointmentsCount;
    }

    public void setAppointmentsCount(Integer appointmentsCount) {
        this.appointmentsCount = appointmentsCount;
    }
}
