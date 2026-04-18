package com.medrec.persistence.patient;

import com.medrec.exception_handling.exceptions.DoctorIsNotGpException;
import com.medrec.persistence.doctor.Doctor;
import jakarta.persistence.*;

@Entity
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String pin;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @Column(name = "is_health_insured", columnDefinition = "boolean default false")
    private boolean isHealthInsured;

    public Patient() {
    }

    public Patient(String firstName, String lastName, String email, String password, String pin, Doctor doctor, boolean isHealthInsured) throws DoctorIsNotGpException {
        if(!doctor.isGp()) {
            throw new DoctorIsNotGpException(
                    String.format("%s %s is not a general practitioner!", doctor.getFirstName(), doctor.getLastName())
            );
        }

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.pin = pin;
        this.doctor = doctor;
        this.isHealthInsured = isHealthInsured;
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

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public boolean isHealthInsured() {
        return isHealthInsured;
    }

    public void setHealthInsured(boolean healthInsured) {
        isHealthInsured = healthInsured;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", doctor=" + String.format("%s %s", doctor.getFirstName(), doctor.getLastName()) +
                ", isHealthInsured=" + isHealthInsured +
                '}';
    }
}
