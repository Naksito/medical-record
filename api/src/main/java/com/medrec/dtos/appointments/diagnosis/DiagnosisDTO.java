package com.medrec.dtos.appointments.diagnosis;

import com.medrec.dtos.appointments.icd.IcdDTO;
import com.medrec.dtos.appointments.sick_leave.SickLeaveDTO;

import java.util.Optional;

public class DiagnosisDTO {
    private int id;
    private String treatmentDescription;
    private IcdDTO icd;
    private Optional<SickLeaveDTO> sickLeave;

    public DiagnosisDTO() {
    }

    public DiagnosisDTO(int id, String treatmentDescription, IcdDTO icd, Optional<SickLeaveDTO> sickLeave) {
        this.id = id;
        this.treatmentDescription = treatmentDescription;
        this.icd = icd;
        this.sickLeave = sickLeave;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTreatmentDescription() {
        return treatmentDescription;
    }

    public void setTreatmentDescription(String treatmentDescription) {
        this.treatmentDescription = treatmentDescription;
    }

    public IcdDTO getIcd() {
        return icd;
    }

    public void setIcd(IcdDTO icd) {
        this.icd = icd;
    }

    public Optional<SickLeaveDTO> getSickLeave() {
        return sickLeave;
    }

    public void setSickLeave(Optional<SickLeaveDTO> sickLeave) {
        this.sickLeave = sickLeave;
    }
}
