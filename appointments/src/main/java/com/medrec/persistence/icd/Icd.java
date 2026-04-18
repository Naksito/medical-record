package com.medrec.persistence.icd;

import com.medrec.persistence.diagnosis.Diagnosis;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Icd {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false, unique = true)
    private String description;

    @OneToMany(mappedBy = "icd", cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    private List<Diagnosis> diagnosis;

    public Icd() {
    }

    public Icd(String code, String description) {
        this.code = code;
        this.description = description;
        this.diagnosis = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public List<Diagnosis> getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(List<Diagnosis> diagnosis) {
        this.diagnosis = diagnosis;
    }

    @Override
    public String toString() {
        return "ICD{" +
            "id=" + id +
            ", code='" + code + '\'' +
            ", description='" + description + '\'' +
            '}';
    }
}
