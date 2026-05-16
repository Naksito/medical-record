package com.medrec.dtos.users.specialty;

public class RegisterSpecialtyDTO {
    private String name;
    private String description;

    public RegisterSpecialtyDTO() {
    }

    public RegisterSpecialtyDTO(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
