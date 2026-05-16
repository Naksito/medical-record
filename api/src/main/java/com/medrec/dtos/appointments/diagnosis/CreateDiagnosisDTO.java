package com.medrec.dtos.appointments.diagnosis;

import com.medrec.exception_handling.exceptions.BadRequestException;
import com.medrec.grpc.appointments.Appointments;

public class CreateDiagnosisDTO {
    private String treatmentDescription;
    private Integer icdId;
    private String sickLeaveDate;
    private Integer sickLeaveDays;

    public CreateDiagnosisDTO() {
    }

    public CreateDiagnosisDTO(String treatmentDescription, int icdId, String sickLeaveDate, Integer sickLeaveDays) {
        this.treatmentDescription = treatmentDescription;
        this.icdId = icdId;
        this.sickLeaveDate = sickLeaveDate;
        this.sickLeaveDays = sickLeaveDays;
    }

    public Appointments.CreateDiagnosisRequest safeConvertToGrpcRequest() {
        Appointments.CreateDiagnosisRequest.Builder requestBuilder = Appointments.CreateDiagnosisRequest.newBuilder();

        if (this.treatmentDescription == null || this.icdId == null) {
            throw new BadRequestException("Treatment description and ICD id are required");
        }

        if ((this.sickLeaveDate != null && this.sickLeaveDays == null) || (this.sickLeaveDate == null && this.sickLeaveDays != null)) {
            throw new BadRequestException("Sick Leave date and number of days are both required");
        }

        requestBuilder.setTreatmentDescription(this.getTreatmentDescription());
        requestBuilder.setIcdId(this.icdId);

        if (this.sickLeaveDate != null) {
            requestBuilder.setSickLeaveDate(this.getSickLeaveDate());
        }

        if (this.sickLeaveDays != null) {
            requestBuilder.setSickLeaveDays(this.getSickLeaveDays());
        }

        return requestBuilder.build();
    }

    public String getTreatmentDescription() {
        return treatmentDescription;
    }

    public void setTreatmentDescription(String treatmentDescription) {
        this.treatmentDescription = treatmentDescription;
    }

    public int getIcdId() {
        return icdId;
    }

    public void setIcdId(int icdId) {
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
