package com.medrec.dtos.auth;

import java.util.List;

public class TokenRequestDTO {
    private String token;
    private String role;

    public TokenRequestDTO() {
    }

    public TokenRequestDTO(String token, String role) {
        this.token = token;
        this.role = role;
    }

    public List<String> getRoleAsList() {
        List<String> roleList = new java.util.ArrayList<>();
        roleList.add(role);
        return roleList;
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
}
