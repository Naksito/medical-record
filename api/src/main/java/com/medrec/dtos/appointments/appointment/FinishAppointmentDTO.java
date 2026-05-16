package com.medrec.dtos.appointments.appointment;

public class FinishAppointmentDTO {
    Integer appointmentId;
    private String treatmentDescription;
    private Integer icdId;
    private String sickLeaveDate;
    private Integer sickLeaveDays;

    public FinishAppointmentDTO() {
    }

    public FinishAppointmentDTO(Integer appointmentId, String treatmentDescription, Integer icdId, String sickLeaveDate, Integer sickLeaveDays) {
        this.appointmentId = appointmentId;
        this.treatmentDescription = treatmentDescription;
        this.icdId = icdId;
        this.sickLeaveDate = sickLeaveDate;
        this.sickLeaveDays = sickLeaveDays;
    }

    public Integer getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getTreatmentDescription() {
        return treatmentDescription;
    }

    public void setTreatmentDescription(String treatmentDescription) {
        this.treatmentDescription = treatmentDescription;
    }

    public Integer getIcdId() {
        return icdId;
    }

    public void setIcdId(Integer icdId) {
        this.icdId = icdId;
    }

    public String getSickLeaveDate() {
        return sickLeaveDate;
    }

    public void setSickLeaveDate(String sickLeaveDate) {
        this.sickLeaveDate = sickLeaveDate;
    }

    public Integer getSickLeaveDays() {
        return sickLeaveDays;
    }

    public void setSickLeaveDays(Integer sickLeaveDays) {
        this.sickLeaveDays = sickLeaveDays;
    }
}
