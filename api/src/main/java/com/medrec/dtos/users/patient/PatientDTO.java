package com.medrec.dtos.users.patient;

import com.medrec.dtos.users.doctor.DoctorDTO;

public class PatientDTO {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String pin;
    private DoctorDTO gp;
    private boolean insured;

    public PatientDTO() {
    }

    public PatientDTO(int id, String firstName, String lastName, String email, String password, String pin, DoctorDTO gp, boolean insured) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.pin = pin;
        this.gp = gp;
        this.insured = insured;
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

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public DoctorDTO getGp() {
        return gp;
    }

    public void setGp(DoctorDTO gp) {
        this.gp = gp;
    }

    public boolean isInsured() {
        return insured;
    }

    public void setInsured(boolean insured) {
        this.insured = insured;
    }
}
