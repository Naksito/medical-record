package com.medrec.persistence.specialty;

import com.medrec.persistence.doctor.Doctor;
import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Specialty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "specialty_name", nullable = false, unique = true)
    private String specialtyName;

    @Column(name = "specialty_description", nullable = false, unique = true)
    private String specialtyDescription;

    @OneToMany(mappedBy = "specialty", cascade = CascadeType.ALL)
    private Set<Doctor> doctors;

    public Specialty() {
    }

    public Specialty(int id, String specialtyName, String specialtyDescription) {
        this.id = id;
        this.specialtyName = specialtyName;
        this.specialtyDescription = specialtyDescription;
    }

    public Specialty(String specialtyName, String specialtyDescription) {
        this.specialtyName = specialtyName;
        this.specialtyDescription = specialtyDescription;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSpecialtyName() {
        return specialtyName;
    }

    public void setSpecialtyName(String specialtyName) {
        this.specialtyName = specialtyName;
    }

    public String getSpecialtyDescription() {
        return specialtyDescription;
    }

    public void setSpecialtyDescription(String specialtyDescription) {
        this.specialtyDescription = specialtyDescription;
    }

    public Set<Doctor> getDoctors() {
        return doctors;
    }

    public void setDoctors(Set<Doctor> doctors) {
        this.doctors = doctors;
    }

    @Override
    public String toString() {
        return "Specialty{" +
                "id=" + id +
                ", specialtyName='" + specialtyName + '\'' +
                ", specialtyDescription='" + specialtyDescription + '\'' +
                '}';
    }
}
