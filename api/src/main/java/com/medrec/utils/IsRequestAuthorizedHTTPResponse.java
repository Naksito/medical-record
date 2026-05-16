package com.medrec.utils;

import java.time.LocalDateTime;

public class IsRequestAuthorizedHTTPResponse {
    public Boolean isValid;
    private String token;
    private String role;
    public String message;
    private String timestamp;

    public IsRequestAuthorizedHTTPResponse(boolean isValid, String token, String role, String message) {
        this.isValid = isValid;
        this.token = token;
        this.role = role;
        this.message = message;
        this.timestamp = LocalDateTime.now().toString();
    }

    public IsRequestAuthorizedHTTPResponse() {
    }

    public Boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
