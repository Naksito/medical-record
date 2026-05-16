package com.medrec.dtos.appointments.appointment;

public class DoctorSickLeaveCountDTO {
    private Integer doctorId;
    private Integer count;

    public DoctorSickLeaveCountDTO() {

    }

    public DoctorSickLeaveCountDTO(Integer doctorId, Integer count) {
        this.doctorId = doctorId;
        this.count = count;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "DoctorSickLeaveCountDTO{" +
            "doctorId=" + doctorId +
            ", count=" + count +
            '}';
    }
}
