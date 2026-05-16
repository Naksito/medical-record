package com.medrec.dtos.general;

import com.medrec.dtos.appointments.appointment.AppointmentDetailedDTO;
import com.medrec.dtos.users.doctor.DoctorDTO;
import com.medrec.dtos.users.patient.PatientDTO;

import java.util.List;

public class DoctorMenuDataDTO {
    private List<PatientDTO> patients;
    private List<DoctorDTO> doctors;
    private List<AppointmentDetailedDTO> appointments;

    public DoctorMenuDataDTO() {
    }

    public DoctorMenuDataDTO(List<PatientDTO> patients, List<DoctorDTO> doctors, List<AppointmentDetailedDTO> appointments) {
        this.patients = patients;
        this.doctors = doctors;
        this.appointments = appointments;
    }

    public List<PatientDTO> getPatients() {
        return patients;
    }

    public void setPatients(List<PatientDTO> patients) {
        this.patients = patients;
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
