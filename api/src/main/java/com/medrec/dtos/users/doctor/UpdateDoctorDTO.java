package com.medrec.dtos.users.doctor;

import com.medrec.exception_handling.exceptions.IdNotSetException;
import com.medrec.grpc.users.Users;

public class UpdateDoctorDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private Boolean generalPractitioner;
    private Integer specialtyId;

    public UpdateDoctorDTO() {
    }

    public UpdateDoctorDTO(int id) {
        this.id = id;
    }

    public UpdateDoctorDTO(Integer id, String firstName, String lastName, Boolean generalPractitioner, Integer specialtyId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.generalPractitioner = generalPractitioner;
        this.specialtyId = specialtyId;
    }

    public Users.UpdateDoctorRequest safeConvertToGrpcRequest() throws RuntimeException {
        Users.UpdateDoctorRequest.Builder requestBuilder = Users.UpdateDoctorRequest.newBuilder();

        if (this.id == null) {
            throw new IdNotSetException("Doctor id is not set. Cannot update doctor");
        }

        requestBuilder.setDoctorId(this.id);

        if (this.firstName != null) {
            requestBuilder.setFirstName(this.firstName);
        }

        if (this.lastName != null) {
            requestBuilder.setLastName(this.lastName);
        }

        if (this.generalPractitioner != null) {
            requestBuilder.setIsGp(this.generalPractitioner);
        }

        if (this.specialtyId != null) {
            requestBuilder.setSpecialtyId(this.specialtyId);
        }

        return requestBuilder.build();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public boolean isGeneralPractitioner() {
        return generalPractitioner;
    }

    public void setGeneralPractitioner(boolean gp) {
        generalPractitioner = gp;
    }

    public int getSpecialtyId() {
        return specialtyId;
    }

    public void setSpecialtyId(int specialtyId) {
        this.specialtyId = specialtyId;
    }
}
