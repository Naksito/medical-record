package com.medrec.services;

import com.medrec.dtos.queries.PatientCountDTO;
import com.medrec.dtos.users.doctor.DoctorDTO;
import com.medrec.dtos.users.doctor.DoctorSummaryDTO;
import com.medrec.dtos.users.doctor.RegisterDoctorDTO;
import com.medrec.dtos.users.doctor.UpdateDoctorDTO;
import com.medrec.dtos.users.patient.PatientDTO;
import com.medrec.dtos.users.patient.RegisterPatientDTO;
import com.medrec.dtos.users.patient.UpdatePatientDTO;
import com.medrec.dtos.users.specialty.RegisterSpecialtyDTO;
import com.medrec.dtos.users.specialty.SpecialtyDTO;
import com.medrec.dtos.users.specialty.UpdateSpecialtyDTO;
import com.medrec.gateways.UsersGateway;
import com.medrec.grpc.users.Users;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class UsersService {
    private final Logger logger = Logger.getLogger(UsersService.class.getName());

    private final UsersGateway usersGateway;

    public UsersService(UsersGateway usersGateway) {
        this.usersGateway = usersGateway;
    }

    public DoctorDTO createDoctor(RegisterDoctorDTO doctor) throws RuntimeException {
        this.logger.info("Creating new doctor");

        Users.CreateDoctorRequest request = Users.CreateDoctorRequest.newBuilder()
            .setFirstName(doctor.getFirstName())
            .setLastName(doctor.getLastName())
            .setEmail(doctor.getEmail())
            .setPassword(doctor.getPassword())
            .setSpecialtyId(doctor.getSpecialtyId())
            .setIsGp(doctor.isGeneralPractitioner())
            .build();

        try {
            Users.Doctor savedDoctor = this.usersGateway.createDoctor(request);
            DoctorDTO doctorDto = getDoctorDto(savedDoctor);

            this.logger.info("Created doctor: " + doctorDto.getId());
            return doctorDto;
        } catch (RuntimeException e) {
            this.logger.info("Error creating doctor: " + e.getMessage());
            throw e;
        }
    }

    public DoctorDTO getDoctorById(int id) throws RuntimeException {
        this.logger.info("Retrieving doctor with id: " + id);

        try {
            Users.Doctor doctor = this.usersGateway.getDoctorById(id);
            DoctorDTO doctorDto = getDoctorDto(doctor);

            this.logger.info("Retrieved doctor: " + doctorDto.getId());
            return doctorDto;
        } catch (RuntimeException e) {
            this.logger.info("Error retrieving doctor: " + e.getMessage());
            throw e;
        }
    }

    public DoctorDTO getDoctorByEmail(String email) throws RuntimeException {
        this.logger.info("Retrieving doctor with email: " + email);

        try {
            Users.Doctor doctor = this.usersGateway.getDoctorByEmail(email);
            DoctorDTO doctorDto = getDoctorDto(doctor);

            this.logger.info("Retrieved doctor: " + doctorDto.getId());
            return doctorDto;
        } catch (RuntimeException e) {
            this.logger.info("Error retrieving doctor: " + e.getMessage());
            throw e;
        }
    }

    public List<DoctorDTO> getAllDoctors() throws RuntimeException {
        this.logger.info("Retrieving all doctors");

        try {
            Users.DoctorList doctors = this.usersGateway.getAllDoctors();
            List<DoctorDTO> doctorDtos = new ArrayList<>();

            doctors.getDoctorsList().forEach(d -> {
                doctorDtos.add(getDoctorDto(d));
            });

            this.logger.info("Retrieved " + doctorDtos.size() + " doctors");
            return doctorDtos;
        } catch (RuntimeException e) {
            this.logger.info("Error retrieving doctors: " + e.getMessage());
            throw e;
        }
    }

    public List<DoctorSummaryDTO> getAllGpDoctors() {
        this.logger.info("Retrieving all GP Doctors");

        try {
            Users.DoctorList gpDoctors = usersGateway.getAllGpDoctors();
            List<DoctorSummaryDTO> doctorDtos = new ArrayList<>();

            gpDoctors.getDoctorsList().forEach(d -> {
                SpecialtyDTO specialtyDto = new SpecialtyDTO(
                    d.getSpecialty().getId(),
                    d.getSpecialty().getSpecialtyName(),
                    d.getSpecialty().getSpecialtyDescription()
                );

                DoctorSummaryDTO dto = new DoctorSummaryDTO(
                    d.getId(),
                    d.getFirstName(),
                    d.getLastName(),
                    d.getIsGp(),
                    specialtyDto
                );

                doctorDtos.add(dto);
            });

            this.logger.info("Retrieved " + doctorDtos.size() + " GP doctors");
            return doctorDtos;
        } catch (RuntimeException e) {
            this.logger.info("Error retrieving GP doctors: " + e.getMessage());
            throw e;
        }
    }

    public DoctorDTO updateDoctor(UpdateDoctorDTO doctor) throws RuntimeException {
        this.logger.info("Updating doctor");

        try {
            Users.Doctor updatedDoctor = this.usersGateway.updateDoctor(doctor.safeConvertToGrpcRequest());
            DoctorDTO doctorDto = getDoctorDto(updatedDoctor);

            this.logger.info("Updated doctor: " + doctorDto.getId());
            return doctorDto;
        } catch (RuntimeException e) {
            this.logger.info("Error updating doctor: " + e.getMessage());
            throw e;
        }
    }

    public void deleteDoctorById(int id) throws RuntimeException {
        this.logger.info("Deleting doctor with id: " + id);

        try {
            this.usersGateway.deleteDoctor(id);
            this.logger.info("Deleted doctor with id: " + id);
        } catch (RuntimeException e) {
            this.logger.info("Error deleting doctor: " + e.getMessage());
            throw e;
        }
    }

    public PatientDTO createPatient(RegisterPatientDTO patient) throws RuntimeException {
        this.logger.info("Creating new patient");

        try {
            Users.CreatePatientRequest request = Users.CreatePatientRequest.newBuilder()
                .setFirstName(patient.getFirstName())
                .setLastName(patient.getLastName())
                .setEmail(patient.getEmail())
                .setPassword(patient.getPassword())
                .setPin(patient.getPin())
                .setGpId(patient.getGpId())
                .setIsHealthInsured(patient.isInsured())
                .build();

            Users.Patient savedPatient = this.usersGateway.createPatient(request);
            PatientDTO patientDto = getPatientDto(savedPatient);

            this.logger.info("Created patient: " + patientDto.getId());
            return patientDto;
        } catch (RuntimeException e) {
            this.logger.info("Error creating patient: " + e.getMessage());
            throw e;
        }
    }

    public PatientDTO getPatientById(int id) throws RuntimeException {
        this.logger.info("Retrieving patient with id: " + id);

        try {
            Users.Patient patient = this.usersGateway.getPatientById(id);
            PatientDTO patientDto = getPatientDto(patient);

            this.logger.info("Retrieved patient: " + patientDto.getId());
            return patientDto;
        } catch (RuntimeException e) {
            this.logger.info("Error retrieving patient: " + e.getMessage());
            throw e;
        }
    }

    public PatientDTO getPatientByEmail(String email) throws RuntimeException {
        this.logger.info("Retrieving patient with email: " + email);

        try {
            Users.Patient patient = this.usersGateway.getPatientByEmail(email);
            PatientDTO patientDto = getPatientDto(patient);

            this.logger.info("Retrieved patient: " + patientDto.getId());
            return patientDto;
        } catch (RuntimeException e) {
            this.logger.info("Error retrieving patient: " + e.getMessage());
            throw e;
        }
    }

    public List<PatientDTO> getAllPatients() throws RuntimeException {
        this.logger.info("Retrieving all patients");

        try {
            Users.PatientList patients = this.usersGateway.getAllPatients();
            List<PatientDTO> patientDtos = new ArrayList<>();

            patients.getPatientsList().forEach(p -> {
                patientDtos.add(getPatientDto(p));
            });

            this.logger.info("Retrieved " + patientDtos.size() + " patients");
            return patientDtos;
        } catch (RuntimeException e) {
            this.logger.info("Error retrieving patients: " + e.getMessage());
            throw e;
        }
    }

    public PatientDTO updatePatient(UpdatePatientDTO patient) throws RuntimeException {
        this.logger.info("Updating patient with id: " + patient.getId());

        try {
            Users.Patient updatedPatient = this.usersGateway.updatePatient(patient.safeConvertToGrpcRequest());
            PatientDTO patientDto = getPatientDto(updatedPatient);

            this.logger.info("Updated patient: " + patientDto.getId());
            return patientDto;
        } catch (RuntimeException e) {
            this.logger.info("Error updating patient: " + e.getMessage());
            throw e;
        }
    }

    public void deletePatientById(int id) throws RuntimeException {
        this.logger.info("Deleting patient with id: " + id);

        try {
            this.usersGateway.deletePatient(id);
            this.logger.info("Deleted patient with id: " + id);
        } catch (RuntimeException e) {
            this.logger.info("Error deleting patient: " + e.getMessage());
            throw e;
        }
    }

    public SpecialtyDTO createSpecialty(RegisterSpecialtyDTO specialtyDto) throws RuntimeException {
        this.logger.info("Creating new specialty");

        Users.CreateSpecialtyRequest request = Users.CreateSpecialtyRequest.newBuilder()
            .setSpecialtyName(specialtyDto.getName())
            .setSpecialtyDescription(specialtyDto.getDescription())
            .build();

        try {
            Users.Specialty specialty = this.usersGateway.createSpecialty(request);
            SpecialtyDTO dto = getSpecialtyDto(specialty);

            this.logger.info("Created specialty: " + dto.getId());
            return dto;
        } catch (RuntimeException e) {
            this.logger.info("Error creating specialty: " + e.getMessage());
            throw e;
        }
    }

    public SpecialtyDTO getSpecialtyById(int id) throws RuntimeException {
        this.logger.info("Retrieving specialty with id: " + id);

        try {
            Users.Specialty specialty = this.usersGateway.getSpecialtyById(id);
            SpecialtyDTO dto = getSpecialtyDto(specialty);

            this.logger.info("Retrieved specialty: " + dto.getId());
            return dto;
        } catch (RuntimeException e) {
            this.logger.info("Error retrieving specialty: " + e.getMessage());
            throw e;
        }
    }

    public List<SpecialtyDTO> getAllSpecialties() throws RuntimeException {
        this.logger.info("Retrieving all specialties");

        Users.SpecialtiesList specialties = usersGateway.getAllSpecialties();
        List<SpecialtyDTO> specialtyDtos = new ArrayList<>();
        specialties.getSpecialtiesList().forEach(specialty -> {
            SpecialtyDTO dto = new SpecialtyDTO(
                specialty.getId(),
                specialty.getSpecialtyName(),
                specialty.getSpecialtyDescription()
            );
            specialtyDtos.add(dto);
        });

        this.logger.info("Retrieved " + specialtyDtos.size() + " specialties");

        return specialtyDtos;
    }

    public SpecialtyDTO updateSpecialty(UpdateSpecialtyDTO specialtyDto) throws RuntimeException {
        this.logger.info("Updating specialty with id: " + specialtyDto.getId());

        try {
            Users.Specialty updatedSpecialty = this.usersGateway.updateSpecialty(specialtyDto.safeConvertToGrpcRequest());
            SpecialtyDTO dto = getSpecialtyDto(updatedSpecialty);

            this.logger.info("Updated specialty: " + dto.getId());
            return dto;
        } catch (RuntimeException e) {
            this.logger.info("Error updating specialty: " + e.getMessage());
            throw e;
        }
    }

    public void deleteSpecialtyById(int id) throws RuntimeException {
        this.logger.info("Deleting specialty with id: " + id);

        try {
            this.usersGateway.deleteSpecialty(id);
            this.logger.info("Deleted specialty with id: " + id);
        } catch (RuntimeException e) {
            this.logger.info("Error deleting specialty: " + e.getMessage());
            throw e;
        }
    }

    public List<PatientDTO> getAllPatientsByGpId(int gpId) throws RuntimeException {
        this.logger.info("Retrieving all patients by GP id: " + gpId);

        try {
            Users.PatientList patients = this.usersGateway.getAllPatientsByGpId(gpId);
            List<PatientDTO> patientDtos = new ArrayList<>();

            patients.getPatientsList().forEach(p -> {
                patientDtos.add(getPatientDto(p));
            });

            this.logger.info("Retrieved " + patientDtos.size() + " patients for GP id: " + gpId);
            return patientDtos;
        } catch (RuntimeException e) {
            this.logger.info("Error retrieving all patients by GP id: " + gpId);
            throw e;
        }
    }

    public List<PatientCountDTO> countOfPatientsForDoctors() throws RuntimeException {
        this.logger.info("Retrieving the count for all patients for every GP doctor");

        try {
            Users.CountOfPatientsForDoctorsResponse response = this.usersGateway.countOfPatientsForDoctors();
            List<PatientCountDTO> patientCountDtos = new ArrayList<>();

            response.getPatientCountListList().forEach(p -> {
                patientCountDtos.add(getPatientCountDto(p));
            });

            this.logger.info("Retrieved list size of all patients for every GP doctor: " + patientCountDtos.size());
            return patientCountDtos;
        } catch (RuntimeException e) {
            this.logger.info("Error retrieving all patients for every GP doctor: " + e.getMessage());
            throw e;
        }
    }

    public List<PatientDTO> getPatientsByListOfIds(List<Integer> ids) throws RuntimeException {
        this.logger.info(String.format("Retrieving all patients by IDs for %d: IDs", ids.size()));

        try {
            Users.PatientList patients = this.usersGateway.getPatientsByListOfIds(ids);
            List<PatientDTO> patientDtos = new ArrayList<>();
            patients.getPatientsList().forEach(p -> {
                patientDtos.add(getPatientDto(p));
            });

            this.logger.info("Retrieved list size of all patients: " + patientDtos.size());
            return patientDtos;
        } catch (RuntimeException e) {
            this.logger.info("Error retrieving all patients: " + e.getMessage());
            throw e;
        }
    }

    private static PatientDTO getPatientDto(Users.Patient patient) {
        Users.Doctor respectiveDoctor = patient.getGp();
        DoctorDTO doctorDto = getDoctorDto(respectiveDoctor);

        return new PatientDTO(
            patient.getId(),
            patient.getFirstName(),
            patient.getLastName(),
            patient.getEmail(),
            patient.getPassword(),
            patient.getPin(),
            doctorDto,
            patient.getIsHealthInsured()
        );
    }

    private static DoctorDTO getDoctorDto(Users.Doctor doctor) {
        SpecialtyDTO specialtyDto = getSpecialtyDto(doctor.getSpecialty());

        return new DoctorDTO(
            doctor.getId(),
            doctor.getFirstName(),
            doctor.getLastName(),
            doctor.getEmail(),
            doctor.getPassword(),
            doctor.getIsGp(),
            specialtyDto
        );
    }

    private static SpecialtyDTO getSpecialtyDto(Users.Specialty specialty) {
        return new SpecialtyDTO(
            specialty.getId(),
            specialty.getSpecialtyName(),
            specialty.getSpecialtyDescription()
        );
    }

    private static PatientCountDTO getPatientCountDto(Users.PatientCount patientCount) {
        return new PatientCountDTO(
            patientCount.getDoctorId(),
            patientCount.getDoctorFirstName(),
            patientCount.getDoctorLastName(),
            patientCount.getPatientCount()
        );
    }
}
