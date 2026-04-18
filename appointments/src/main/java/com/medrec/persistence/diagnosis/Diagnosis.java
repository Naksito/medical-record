package com.medrec.persistence.diagnosis;

import com.medrec.persistence.appointment.Appointment;
import com.medrec.persistence.icd.Icd;
import com.medrec.persistence.leave.SickLeave;
import jakarta.persistence.*;

@Entity
public class Diagnosis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String treatmentDescription;

    @ManyToOne
    @JoinColumn(name = "icd_id", nullable = false)
    private Icd icd;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sick_leave_id", referencedColumnName = "id")
    private SickLeave sickLeave;

    @OneToOne(mappedBy = "diagnosis")
    private Appointment appointment;

    public Diagnosis() {
    }

    public Diagnosis(String treatmentDescription, Icd icd) {
        this.treatmentDescription = treatmentDescription;
        this.icd = icd;
    }

    public Diagnosis(String treatmentDescription, Icd icd, SickLeave leave) {
        this.treatmentDescription = treatmentDescription;
        this.icd = icd;
        this.sickLeave = leave;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTreatmentDescription() {
        return treatmentDescription;
    }

    public void setTreatmentDescription(String treatmentDescription) {
        this.treatmentDescription = treatmentDescription;
    }

    public Icd getIcd() {
        return icd;
    }

    public void setIcd(Icd icd) {
        this.icd = icd;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public SickLeave getSickLeave() {
        return sickLeave;
    }

    public void setSickLeave(SickLeave sickLeave) {
        this.sickLeave = sickLeave;
    }

    @Override
    public String toString() {
        return "Diagnosis{" +
            "id=" + id +
            ", treatmentDescription='" + treatmentDescription + '\'' +
            ", icd=" + icd +
            ", sickLeave=" + sickLeave +
            '}';
    }
}
