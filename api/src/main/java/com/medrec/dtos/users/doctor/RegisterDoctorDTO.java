package com.medrec.dtos.users.doctor;

public class RegisterDoctorDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private boolean generalPractitioner;
    private int specialtyId;

    public RegisterDoctorDTO() {
    }

    public RegisterDoctorDTO(String firstName, String lastName, String email, String password, boolean generalPractitioner, int specialtyId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.generalPractitioner = generalPractitioner;
        this.specialtyId = specialtyId;
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

    @Override
    public String toString() {
        return "RegisterDoctorDTO{" +
            "firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            ", password='" + password + '\'' +
            ", generalPractitioner=" + generalPractitioner +
            ", specialtyId='" + specialtyId + '\'' +
            '}';
    }
}
