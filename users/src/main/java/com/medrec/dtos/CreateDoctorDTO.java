package com.medrec.dtos;

import com.medrec.persistence.doctor.Doctor;
import com.medrec.persistence.specialty.Specialty;

public class CreateDoctorDTO {
    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private int specialtyId;

    private boolean isGp;

    public CreateDoctorDTO(String firstName, String lastName, String email, String password, int specialtyId, boolean isGp) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.specialtyId = specialtyId;
        this.isGp = isGp;
    }

    public Doctor createDoctorWithSpecialty(Specialty specialty) {
        return new Doctor(
            this.firstName,
            this.lastName,
            this.email,
            this.password,
            specialty,
            this.isGp
        );
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSpecialtyId() {
        return specialtyId;
    }

    public void setSpecialtyId(int specialtyId) {
        this.specialtyId = specialtyId;
    }

    public boolean isGp() {
        return isGp;
    }

    public void setGp(boolean gp) {
        isGp = gp;
    }
}
