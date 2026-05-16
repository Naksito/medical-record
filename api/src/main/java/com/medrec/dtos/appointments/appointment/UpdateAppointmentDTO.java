package com.medrec.dtos.appointments.appointment;

import com.medrec.exception_handling.exceptions.IdNotSetException;
import com.medrec.grpc.appointments.Appointments;

public class UpdateAppointmentDTO {
    private Integer id;
    private String status;
    private Integer diagnosisId;

    public UpdateAppointmentDTO() {
    }

    public UpdateAppointmentDTO(Integer id, String status, Integer diagnosisId) {
        this.id = id;
        this.status = status;
        this.diagnosisId = diagnosisId;
    }

    public Appointments.UpdateAppointmentRequest safeConvertToGrpcRequest() throws RuntimeException{
        Appointments.UpdateAppointmentRequest.Builder requestBuilder = Appointments.UpdateAppointmentRequest.newBuilder();

        if (this.id == null) {
            throw new IdNotSetException("Appointment id is not set. Cannot update appointment");
        }

        requestBuilder.setId(this.id);

        if (this.status != null) {
            requestBuilder.setStatus(this.status);
        }

        if (this.diagnosisId != null) {
            requestBuilder.setDiagnosisId(this.diagnosisId);
        }

        return requestBuilder.build();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getDiagnosisId() {
        return diagnosisId;
    }

    public void setDiagnosisId(Integer diagnosisId) {
        this.diagnosisId = diagnosisId;
    }
}
