package com.medrec.dtos;

public class PatientCountDTO {
    private Integer doctorId;
    private String doctorFirstName;
    private String doctorLastName;
    private Integer patientCount;

    public PatientCountDTO() {
    }

    public PatientCountDTO(Integer doctorId, String doctorFirstName, String doctorLastName, Integer patientCount) {
        this.doctorId = doctorId;
        this.doctorFirstName = doctorFirstName;
        this.doctorLastName = doctorLastName;
        this.patientCount = patientCount;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorFirstName() {
        return doctorFirstName;
    }

    public void setDoctorFirstName(String doctorFirstName) {
        this.doctorFirstName = doctorFirstName;
    }

    public String getDoctorLastName() {
        return doctorLastName;
    }

    public void setDoctorLastName(String doctorLastName) {
        this.doctorLastName = doctorLastName;
    }

    public Integer getPatientCount() {
        return patientCount;
    }

    public void setPatientCount(Integer patientCount) {
        this.patientCount = patientCount;
    }

    @Override
    public String toString() {
        return "PatientCountDTO{" +
            "doctorId=" + doctorId +
            ", doctorFirstName='" + doctorFirstName + '\'' +
            ", doctorLastName='" + doctorLastName + '\'' +
            ", patientCount=" + patientCount +
            '}';
    }
}
