package com.medrec.dtos.general;

import com.medrec.dtos.appointments.appointment.AppointmentDetailedDTO;
import com.medrec.dtos.users.doctor.DoctorDTO;
import com.medrec.dtos.users.patient.PatientDTO;

import java.util.List;

public class PatientMenuDataDTO {
    private PatientDTO patient;
    private List<DoctorDTO> doctors;
    private List<AppointmentDetailedDTO> appointments;

    public PatientMenuDataDTO() {
    }

    public PatientMenuDataDTO(PatientDTO patient, List<DoctorDTO> doctors, List<AppointmentDetailedDTO> appointments) {
        this.patient = patient;
        this.doctors = doctors;
        this.appointments = appointments;
    }

    public PatientDTO getPatient() {
        return patient;
    }

    public void setPatient(PatientDTO patient) {
        this.patient = patient;
    }

    public List<DoctorDTO> getDoctors() {
        return doctors;
    }

    public void setDoctors(List<DoctorDTO> doctors) {
        this.doctors = doctors;
    }

    public List<AppointmentDetailedDTO> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<AppointmentDetailedDTO> appointments) {
        this.appointments = appointments;
    }
}
