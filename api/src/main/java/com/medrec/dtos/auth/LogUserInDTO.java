package com.medrec.dtos.auth;

public class LogUserInDTO {
    private String email;
    private String password;

    public LogUserInDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public LogUserInDTO() {
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
}
