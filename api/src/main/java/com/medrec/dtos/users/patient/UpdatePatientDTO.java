package com.medrec.dtos.users.patient;

import com.medrec.exception_handling.exceptions.IdNotSetException;
import com.medrec.grpc.users.Users;

public class UpdatePatientDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private Integer gpId;
    private Boolean insured;

    public UpdatePatientDTO() {
    }

    public UpdatePatientDTO(Integer id) {
        this.id = id;
    }

    public UpdatePatientDTO(Integer id, String firstName, String lastName, Integer gpId, Boolean insured) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gpId = gpId;
        this.insured = insured;
    }

    public Users.UpdatePatientRequest safeConvertToGrpcRequest() throws RuntimeException {
        Users.UpdatePatientRequest.Builder requestBuilder = Users.UpdatePatientRequest.newBuilder();

        if (this.id == null) {
            throw new IdNotSetException("Patient id is not set. Cannot update patient");
        }

        requestBuilder.setPatientId(this.id);

        if (this.firstName != null) {
            requestBuilder.setFirstName(this.firstName);
        }

        if (this.lastName != null) {
            requestBuilder.setLastName(this.lastName);
        }

        if (this.gpId != null) {
            requestBuilder.setGpId(this.gpId);
        }

        if (this.insured != null) {
            requestBuilder.setIsHealthInsured(this.insured);
        }

        return requestBuilder.build();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getGpId() {
        return gpId;
    }

    public void setGpId(Integer gpId) {
        this.gpId = gpId;
    }

    public Boolean getInsured() {
        return insured;
    }

    public void setInsured(Boolean insured) {
        this.insured = insured;
    }
}
