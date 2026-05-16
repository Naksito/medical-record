package com.medrec.dtos.users.doctor;

import com.medrec.dtos.users.specialty.SpecialtyDTO;

public class DoctorSummaryDTO {
    private int id;
    private String firstName;
    private String lastName;
    private String fullName;
    private boolean isGp;
    private SpecialtyDTO specialty;

    public DoctorSummaryDTO() {
    }

    public DoctorSummaryDTO(int id, String firstName, String lastName, boolean isGp, SpecialtyDTO specialty) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = firstName + " " + lastName;
        this.isGp = isGp;
        this.specialty = specialty;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isGp() {
        return isGp;
    }

    public void setGp(boolean gp) {
        isGp = gp;
    }

    public SpecialtyDTO getSpecialty() {
        return specialty;
    }

    public void setSpecialty(SpecialtyDTO specialty) {
        this.specialty = specialty;
    }
}
