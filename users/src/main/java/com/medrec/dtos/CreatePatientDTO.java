package com.medrec.dtos;

import com.medrec.exception_handling.exceptions.DoctorIsNotGpException;
import com.medrec.persistence.doctor.Doctor;
import com.medrec.persistence.patient.Patient;

import java.util.logging.Logger;

public class CreatePatientDTO {
    private final Logger logger = Logger.getLogger(CreatePatientDTO.class.getName());

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String pin;

    private int gpId;

    private boolean isHealthInsured;

    public CreatePatientDTO(String firstName, String lastName, String email, String password, String pin, int gpId, boolean isHealthInsured) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.pin = pin;
        this.gpId = gpId;
        this.isHealthInsured = isHealthInsured;
    }

    public Patient getDomainModel(Doctor doctor) {
        try {
            return new Patient(
                this.firstName,
                this.lastName,
                this.email,
                this.password,
                this.pin,
                doctor,
                this.isHealthInsured
            );
        } catch (DoctorIsNotGpException e) {
            this.logger.severe(e.getMessage());
            return null;
        }
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

    public boolean isHealthInsured() {
        return isHealthInsured;
    }

    public void setHealthInsured(boolean healthInsured) {
        isHealthInsured = healthInsured;
    }
}
