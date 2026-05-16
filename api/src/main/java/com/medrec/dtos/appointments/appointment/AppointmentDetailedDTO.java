package com.medrec.dtos.appointments.appointment;

import com.medrec.dtos.appointments.diagnosis.DiagnosisDTO;
import com.medrec.dtos.users.doctor.DoctorDTO;
import com.medrec.dtos.users.patient.PatientDTO;

import java.util.Optional;

public class AppointmentDetailedDTO {
    private int id;
    private String date;
    private String time;
    private Optional<DoctorDTO> doctor;
    private Optional<PatientDTO> patient;
    private String status;
    private Optional<DiagnosisDTO> diagnosis;
    private boolean performedByGp;

    public AppointmentDetailedDTO() {
    }

    public AppointmentDetailedDTO(int id, String date, String time, Optional<DoctorDTO> doctor, Optional<PatientDTO> patient, String status, Optional<DiagnosisDTO> diagnosis, boolean performedByGp) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.doctor = doctor;
        this.patient = patient;
        this.status = status;
        this.diagnosis = diagnosis;
        this.performedByGp = performedByGp;
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

    public Optional<DoctorDTO> getDoctor() {
        return doctor;
    }

    public void setDoctor(Optional<DoctorDTO> doctor) {
        this.doctor = doctor;
    }

    public Optional<PatientDTO> getPatient() {
        return patient;
    }

    public void setPatient(Optional<PatientDTO> patient) {
        this.patient = patient;
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

    public boolean isPerformedByGp() {
        return performedByGp;
    }

    public void setPerformedByGp(boolean performedByGp) {
        this.performedByGp = performedByGp;
    }
}
