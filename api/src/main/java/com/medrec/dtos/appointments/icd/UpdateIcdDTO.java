package com.medrec.dtos.appointments.icd;

import com.medrec.exception_handling.exceptions.IdNotSetException;
import com.medrec.grpc.appointments.Appointments;

public class UpdateIcdDTO {
    private Integer id;
    private String code;
    private String description;

    public UpdateIcdDTO() {
    }

    public UpdateIcdDTO(Integer id, String code, String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }

    public Appointments.UpdateIcdRequest safeConvertToGrpcRequest() {
        Appointments.UpdateIcdRequest.Builder requestBuilder = Appointments.UpdateIcdRequest.newBuilder();

        if (this.id == null) {
            throw new IdNotSetException("ICD id is not set. Cannot update ICD");
        }

        requestBuilder.setId(this.id);

        if (this.code != null) {
            requestBuilder.setCode(this.code);
        }

        if (this.description != null) {
            requestBuilder.setDescription(this.description);
        }

        return requestBuilder.build();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
