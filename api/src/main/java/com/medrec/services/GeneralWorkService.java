package com.medrec.services;

import com.medrec.dtos.appointments.appointment.AppointmentDTO;
import com.medrec.dtos.appointments.appointment.AppointmentDetailedDTO;
import com.medrec.dtos.general.DoctorMenuDataDTO;
import com.medrec.dtos.general.PatientMenuDataDTO;
import com.medrec.dtos.users.doctor.DoctorDTO;
import com.medrec.dtos.users.patient.PatientDTO;
import com.medrec.exception_handling.exceptions.ConcurrentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class GeneralWorkService {
    private final Logger logger = Logger.getLogger(GeneralWorkService.class.getName());
    private final Executor executor;
    private final AppointmentsService appointmentsService;
    private final UsersService usersService;

    @Autowired
    public GeneralWorkService(@Qualifier("executor") Executor executor, AppointmentsService appointmentsService, UsersService usersService) {
        this.executor = executor;
        this.appointmentsService = appointmentsService;
        this.usersService = usersService;
    }

    public PatientMenuDataDTO getPatientMenuData(int patientId) throws RuntimeException {
        CompletableFuture<List<DoctorDTO>> doctorsFuture = CompletableFuture.supplyAsync(() -> {
            String threadName = Thread.currentThread().getName();
            this.logger.info(String.format("Getting all doctors for patient menu (patient - %d) on thread (%s)", patientId, threadName));

            try {
                List<DoctorDTO> doctors = this.usersService.getAllDoctors();
                this.logger.info("Found " + doctors.size() + " doctors");
                return doctors;
            } catch (Exception e) {
                this.logger.warning(e.getMessage());
                throw new ConcurrentException("Error getting doctors for patient menu");
            }
        }, executor);

        CompletableFuture<List<AppointmentDTO>> appointmentsFuture = CompletableFuture.supplyAsync(() -> {
            String threadName = Thread.currentThread().getName();
            this.logger.info(String.format("Getting all appointments for patient menu (patient - %d) on thread (%s)", patientId, threadName));

            try {
                List<AppointmentDTO> appointments = this.appointmentsService.getAllByPatientId(patientId);
                this.logger.info("Found " + appointments.size() + " appointments");
                return appointments;
            } catch (Exception e) {
                this.logger.warning(e.getMessage());
                throw new ConcurrentException("Error getting appointments for patient menu");
            }
        }, executor);

        CompletableFuture<PatientDTO> patientFuture = CompletableFuture.supplyAsync(() -> {
            String threadName = Thread.currentThread().getName();
            this.logger.info(String.format("Getting current patient for patient menu (patient - %d) on thread (%s)", patientId, threadName));

            try {
                PatientDTO patient = this.usersService.getPatientById(patientId);
                this.logger.info("Found patient with id " + patientId);
                return patient;
            } catch (Exception e) {
                this.logger.warning(e.getMessage());
                throw new ConcurrentException("Error getting current patient for patient menu");
            }
        }, executor);

        CompletableFuture<Void> done = CompletableFuture.allOf(doctorsFuture, appointmentsFuture, patientFuture);

        done.join();

        try {
            List<DoctorDTO> doctors = doctorsFuture.get();
            List<AppointmentDTO> appointments = appointmentsFuture.get();
            PatientDTO patient = patientFuture.get();
            this.logger.info(String.format("Patient Menu Data: (doctors - %d, appointments - %d)", doctors.size(), appointments.size()));

            return new PatientMenuDataDTO(
                patient,
                doctors,
                this.matchPatientMenuData(patient, doctors, appointments)
            );
        } catch (ExecutionException | InterruptedException e) {
            this.logger.severe(e.getMessage());
            throw new ConcurrentException("Error getting patient menu");
        }
    }

    private List<AppointmentDetailedDTO> matchPatientMenuData(PatientDTO patient, List<DoctorDTO> doctors, List<AppointmentDTO> appointments) {
        Map<Integer, DoctorDTO> doctorMap = doctors.stream().collect(Collectors.toMap(DoctorDTO::getId, doctor -> doctor));

        return appointments.stream()
            .map(appointment -> {
                DoctorDTO doctor = doctorMap.get(appointment.getDoctorId());
                boolean isGp = false;

                if (doctor != null) {
                    isGp = patient.getGp().getId() == doctor.getId();
                }

                return new AppointmentDetailedDTO(
                    appointment.getId(),
                    appointment.getDate(),
                    appointment.getTime(),
                    Optional.ofNullable(doctor),
                    Optional.empty(),
                    appointment.getStatus(),
                    appointment.getDiagnosis(),
                    isGp
                );
            })
            .collect(Collectors.toList());
    }

    public DoctorMenuDataDTO getDoctorMenuData(int doctorId) throws RuntimeException {
        CompletableFuture<List<DoctorDTO>> doctorsFuture = CompletableFuture.supplyAsync(() -> {
            String threadName = Thread.currentThread().getName();
            this.logger.info(String.format("Getting all doctors for doctor menu (doctor - %d) on thread (%s)", doctorId, threadName));

            try {
                List<DoctorDTO> doctors = this.usersService.getAllDoctors();
                this.logger.info("Found " + doctors.size() + " doctors");
                return doctors;
            } catch (Exception e) {
                this.logger.warning(e.getMessage());
                throw new ConcurrentException("Error getting doctors for patient menu");
            }
        }, executor);

        CompletableFuture<List<AppointmentDTO>> appointmentsFuture = CompletableFuture.supplyAsync(() -> {
            String threadName = Thread.currentThread().getName();
            this.logger.info(String.format("Getting all appointments for doctor menu (doctor - %d) on thread (%s)", doctorId, threadName));

            try {
                List<AppointmentDTO> appointments = this.appointmentsService.getAllByDoctorId(doctorId);
                this.logger.info("Found " + appointments.size() + " appointments");
                return appointments;
            } catch (Exception e) {
                this.logger.warning(e.getMessage());
                throw new ConcurrentException("Error getting appointments for patient menu");
            }
        }, executor);

        CompletableFuture<List<PatientDTO>> patientsFuture = CompletableFuture.supplyAsync(() -> {
            String threadName = Thread.currentThread().getName();
            this.logger.info(String.format("Getting all patients for doctor menu (doctor - %d) on thread (%s)", doctorId, threadName));

            try {
                List<PatientDTO> patients = this.usersService.getAllPatients();
                this.logger.info("Found " + patients.size() + " patients");
                return patients;
            } catch (Exception e) {
                this.logger.warning(e.getMessage());
                throw new ConcurrentException("Error getting current patient for patient menu");
            }
        }, executor);

        CompletableFuture<Void> done = CompletableFuture.allOf(doctorsFuture, appointmentsFuture, patientsFuture);

        done.join();

        try {
            List<DoctorDTO> doctors = doctorsFuture.get();
            List<AppointmentDTO> appointments = appointmentsFuture.get();
            List<PatientDTO> patients = patientsFuture.get();
            this.logger.info(String.format("Doctor Menu Data: (doctors - %d, appointments - %d)", doctors.size(), appointments.size()));

            return new DoctorMenuDataDTO(
                patients,
                doctors,
                this.matchDoctorMenuData(
                    this.getCurrentDoctor(doctorId, doctors),
                    patients,
                    appointments
                )
            );
        } catch (ExecutionException | InterruptedException e) {
            this.logger.severe(e.getMessage());
            throw new ConcurrentException("Error getting patient menu");
        }
    }

    private Optional<DoctorDTO> getCurrentDoctor(int id, List<DoctorDTO> doctors) {
        for (DoctorDTO doctor : doctors) {
            if (doctor.getId() == id) {
                return Optional.of(doctor);
            }
        }
        return Optional.empty();
    }

    private List<AppointmentDetailedDTO> matchDoctorMenuData(Optional<DoctorDTO> doctor, List<PatientDTO> patients, List<AppointmentDTO> appointments) {
        Map<Integer, PatientDTO> patientsMap = patients.stream().collect(Collectors.toMap(PatientDTO::getId, patient -> patient));

        return appointments.stream()
            .map(appointment -> {
                PatientDTO patient = patientsMap.get(appointment.getPatientId());

                boolean isGp = false;
                if (patient != null && doctor.isPresent()) {
                    isGp = patient.getGp().getId() == doctor.get().getId();
                }

                return new AppointmentDetailedDTO(
                    appointment.getId(),
                    appointment.getDate(),
                    appointment.getTime(),
                    Optional.empty(),
                    Optional.ofNullable(patient),
                    appointment.getStatus(),
                    appointment.getDiagnosis(),
                    isGp
                );
            })
            .collect(Collectors.toList());
    }

    public List<PatientDTO> pipelinePatientsFromIcdId(int icdId) {
        this.logger.info("Pipelining Patients from ICD ID: " + icdId);

        try {
            List<Integer> ids = this.appointmentsService.getAllPatientIdsForIcd(icdId);
            List<PatientDTO> patients = this.usersService.getPatientsByListOfIds(ids);

            this.logger.info("Found " + patients.size() + " patients for ICD ID: " + icdId);
            return patients;
        } catch (RuntimeException e) {
            this.logger.warning("Error getting patient list for ICD: " + e.getMessage());
            throw e;
        }
    }
}
