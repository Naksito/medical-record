package com.medrec.persistence.leave;

import com.medrec.persistence.diagnosis.Diagnosis;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class SickLeave {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private int daysOfLeave;

    @OneToOne(mappedBy = "sickLeave")
    private Diagnosis diagnosis;

    public SickLeave() {
    }

    public SickLeave(LocalDate startDate, int daysOfLeave) {
        this.startDate = startDate;
        this.daysOfLeave = daysOfLeave;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public int getDaysOfLeave() {
        return daysOfLeave;
    }

    public void setDaysOfLeave(int daysOfLeave) {
        this.daysOfLeave = daysOfLeave;
    }

    public Diagnosis getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(Diagnosis diagnosis) {
        this.diagnosis = diagnosis;
    }

    @Override
    public String toString() {
        return "SickLeave{" +
            "daysOfLeave=" + daysOfLeave +
            ", startDate=" + startDate +
            ", id=" + id +
            '}';
    }
}
