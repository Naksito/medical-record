package com.medrec.dtos.appointments.appointment;

import com.medrec.dtos.appointments.diagnosis.DiagnosisDTO;

import java.util.Optional;

public class AppointmentDTO {
    private int id;
    private String date;
    private String time;
    private int doctorId;
    private int patientId;
    private String status;
    private Optional<DiagnosisDTO> diagnosis;

    public AppointmentDTO() {
    }

    public AppointmentDTO(int id, String date, String time, int doctorId, int patientId, String status, Optional<DiagnosisDTO> diagnosis) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.status = status;
        this.diagnosis = diagnosis;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Optional<DiagnosisDTO> getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(Optional<DiagnosisDTO> diagnosis) {
        this.diagnosis = diagnosis;
    }
}
