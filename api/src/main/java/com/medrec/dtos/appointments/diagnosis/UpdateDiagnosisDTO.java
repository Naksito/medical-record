package com.medrec.dtos.appointments.diagnosis;

import com.medrec.exception_handling.exceptions.BadRequestException;
import com.medrec.exception_handling.exceptions.IdNotSetException;
import com.medrec.grpc.appointments.Appointments;

public class UpdateDiagnosisDTO {
    private Integer id;
    private String treatmentDescription;
    private Integer icdId;
    private String sickLeaveDate;
    private Integer sickLeaveDays;

    public UpdateDiagnosisDTO() {
    }

    public UpdateDiagnosisDTO(Integer id, String treatmentDescription, Integer icdId, String sickLeaveDate, Integer sickLeaveDays) {
        this.id = id;
        this.treatmentDescription = treatmentDescription;
        this.icdId = icdId;
        this.sickLeaveDate = sickLeaveDate;
        this.sickLeaveDays = sickLeaveDays;
    }

    public Appointments.UpdateDiagnosisRequest safeConvertToGrpcRequest() {
        Appointments.UpdateDiagnosisRequest.Builder requestBuilder = Appointments.UpdateDiagnosisRequest.newBuilder();

        if (this.id == null) {
            throw new IdNotSetException("Diagnosis id is not set. Cannot update diagnosis");
        }

        requestBuilder.setId(this.id);

        if (this.treatmentDescription != null) {
            requestBuilder.setTreatmentDescription(this.treatmentDescription);
        }

        if (this.icdId != null) {
            requestBuilder.setIcdId(this.icdId);
        }

        if ((this.sickLeaveDate != null && this.sickLeaveDays == null) || (this.sickLeaveDate == null && this.sickLeaveDays != null)) {
            throw new BadRequestException("Sick Leave date and number of days are both required");
        }

        if (this.sickLeaveDate != null && this.sickLeaveDays != null) {
            requestBuilder.setSickLeaveDate(this.getSickLeaveDate());
            requestBuilder.setSickLeaveDays(this.getSickLeaveDays());
        }

        return requestBuilder.build();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
