package com.medrec.dtos.appointments.icd;

public class CreateIcdDTO {
    private String code;
    private String description;

    public CreateIcdDTO() {
    }

    public CreateIcdDTO(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
