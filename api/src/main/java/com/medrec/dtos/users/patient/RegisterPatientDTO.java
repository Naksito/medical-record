package com.medrec.dtos.users.patient;


public class RegisterPatientDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String pin;
    private int gpId;
    private boolean insured;

    public RegisterPatientDTO() {
    }

    public RegisterPatientDTO(String firstName, String lastName, String email, String password, String pin, int gpId, boolean insured) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.pin = pin;
        this.gpId = gpId;
        this.insured = insured;
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

    public int getGpId() {
        return gpId;
    }

    public void setGpId(int gpId) {
        this.gpId = gpId;
    }

    public boolean isInsured() {
        return insured;
    }

    public void setInsured(boolean healthInsured) {
        insured = healthInsured;
    }

    @Override
    public String toString() {
        return "RegisterPatientDTO{" +
            "firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            ", pin='" + pin + '\'' +
            ", gpId=" + gpId +
            ", insured=" + insured +
            '}';
    }
}
