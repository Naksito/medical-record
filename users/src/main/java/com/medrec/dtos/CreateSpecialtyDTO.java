package com.medrec.dtos;

import com.medrec.persistence.specialty.Specialty;

public class CreateSpecialtyDTO {
    private String name;
    private String description;

    public CreateSpecialtyDTO(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public CreateSpecialtyDTO() {
    }

    public Specialty getDomainModel() {
        return new Specialty(this.name, this.description);
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
