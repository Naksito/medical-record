package com.medrec.dtos.users.doctor;

import com.medrec.dtos.users.specialty.SpecialtyDTO;

public class DoctorDTO {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private boolean isGp;
    private SpecialtyDTO specialty;

    public DoctorDTO() {
    }

    public DoctorDTO(int id, String firstName, String lastName, String email, String password, boolean isGp, SpecialtyDTO specialty) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
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
