package com.medrec.services;

import com.google.protobuf.Int32Value;
import com.medrec.dtos.appointments.appointment.*;
import com.medrec.dtos.appointments.diagnosis.CreateDiagnosisDTO;
import com.medrec.dtos.appointments.diagnosis.DiagnosisDTO;
import com.medrec.dtos.appointments.diagnosis.UpdateDiagnosisDTO;
import com.medrec.dtos.appointments.icd.CreateIcdDTO;
import com.medrec.dtos.appointments.icd.IcdDTO;
import com.medrec.dtos.appointments.icd.IcdOccurrenceDTO;
import com.medrec.dtos.appointments.icd.UpdateIcdDTO;
import com.medrec.dtos.appointments.sick_leave.CreateSickLeaveDTO;
import com.medrec.dtos.appointments.sick_leave.SickLeaveDTO;
import com.medrec.dtos.appointments.sick_leave.UpdateSickLeaveDTO;
import com.medrec.gateways.AppointmentsGateway;
import com.medrec.grpc.appointments.Appointments;
import com.medrec.utils.Utils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class AppointmentsService {
    private final Logger logger = Logger.getLogger(AppointmentsService.class.getName());

    private final AppointmentsGateway appointmentsGateway;

    public AppointmentsService(AppointmentsGateway appointmentsGateway) {
        this.appointmentsGateway = appointmentsGateway;
    }

    public AppointmentDTO createAppointment(CreateAppointmentDTO dto) throws RuntimeException {
        this.logger.info("Creating new appointment");

        Appointments.CreateAppointmentRequest request = Appointments.CreateAppointmentRequest.newBuilder()
            .setDate(dto.getDate())
            .setTime(dto.getTime())
            .setDoctorId(dto.getDoctorId())
            .setPatientId(dto.getPatientId())
            .build();

        try {
            Appointments.Appointment response = this.appointmentsGateway.createAppointment(request);
            AppointmentDTO appointmentDTO = Utils.getDTOFromAppointmentsGrpc(response);

            this.logger.info("New appointment created with id: " + appointmentDTO.getId());
            return appointmentDTO;
        } catch (RuntimeException e) {
            this.logger.warning("Could not create new appointment");
            throw e;
        }
    }

    public AppointmentDTO getAppointmentById(int id) {
        this.logger.info("Retrieving appointment with id: " + id);

        try {
            Appointments.Appointment appointment = this.appointmentsGateway.getAppointmentById(id);
            AppointmentDTO appointmentDTO = Utils.getDTOFromAppointmentsGrpc(appointment);

            this.logger.info("Appointment retrieved with id: " + appointmentDTO.getId());
            return appointmentDTO;
        } catch (RuntimeException e) {
            this.logger.warning("Could not retrieve appointment with id: " + id);
            throw e;
        }
    }

    public List<AppointmentDTO> getAllAppointments() {
        this.logger.info("Retrieving all appointments");

        try {
            Appointments.AppointmentsList appointments = this.appointmentsGateway.getAllAppointments();
            List<AppointmentDTO> appointmentDTOs = appointments.getAppointmentsList().stream()
                .map(Utils::getDTOFromAppointmentsGrpc)
                .toList();

            this.logger.info(appointmentDTOs.size() + "appointments retrieved");
            return appointmentDTOs;
        } catch (RuntimeException e) {
            this.logger.warning("Could not retrieve all appointments");
            throw e;
        }
    }

    public List<AppointmentDTO> getAllByPatientEmail(String email) {
        this.logger.info("Retrieving all appointments for patient with email: " + email);

        try {
            Appointments.AppointmentsList appointments = this.appointmentsGateway.getAllByPatientEmail(email);
            List<AppointmentDTO> appointmentDTOs = appointments.getAppointmentsList().stream()
                .map(Utils::getDTOFromAppointmentsGrpc)
                .toList();

            this.logger.info(appointmentDTOs.size() + "appointments retrieved for patient with email: " + email);
            return appointmentDTOs;
        } catch (RuntimeException e) {
            this.logger.warning("Could not retrieve all appointments for patient with email: " + email);
            throw e;
        }
    }

    public List<AppointmentDTO> getAllByPatientId(int id) {
        this.logger.info("Retrieving all appointments for patient with id: " + id);

        try {
            Appointments.AppointmentsList appointments = this.appointmentsGateway.getAllByPatientId(id);
            List<AppointmentDTO> appointmentDTOs = appointments.getAppointmentsList().stream()
                .map(Utils::getDTOFromAppointmentsGrpc)
                .toList();

            this.logger.info(appointmentDTOs.size() + "appointments retrieved for patient with id: " + id);
            return appointmentDTOs;
        } catch (RuntimeException e) {
            this.logger.warning("Could not retrieve all appointments for patient with id: " + id);
            throw e;
        }
    }

    public List<AppointmentDTO> getAllByDoctorEmail(String email) {
        this.logger.info("Retrieving all appointments for doctor with email: " + email);

        try {
            Appointments.AppointmentsList appointments = this.appointmentsGateway.getAllByDoctorEmail(email);
            List<AppointmentDTO> appointmentDTOs = appointments.getAppointmentsList().stream()
                .map(Utils::getDTOFromAppointmentsGrpc)
                .toList();

            this.logger.info(appointmentDTOs.size() + " appointments retrieved for doctor with email: " + email);
            return appointmentDTOs;
        } catch (RuntimeException e) {
            this.logger.warning("Could not retrieve all appointments for doctor with email: " + email);
            throw e;
        }
    }

    public List<AppointmentDTO> getAllByDoctorId(int id) {
        this.logger.info("Retrieving all appointments for doctor with id: " + id);

        try {
            Appointments.AppointmentsList appointments = this.appointmentsGateway.getAllByDoctorId(id);
            List<AppointmentDTO> appointmentDTOs = appointments.getAppointmentsList().stream()
                .map(Utils::getDTOFromAppointmentsGrpc)
                .toList();

            this.logger.info(appointmentDTOs.size() + "appointments retrieved for doctor with id: " + id);
            return appointmentDTOs;
        } catch (RuntimeException e) {
            this.logger.warning("Could not retrieve all appointments for doctor with id: " + id);
            throw e;
        }
    }

    public AppointmentDTO updateAppointment(UpdateAppointmentDTO dto) throws RuntimeException {
        this.logger.info("Updating appointment with id " + dto.getId());

        try {
            Appointments.Appointment updatedAppointment = this.appointmentsGateway.updateAppointment(dto.safeConvertToGrpcRequest());
            AppointmentDTO appointmentDTO = Utils.getDTOFromAppointmentsGrpc(updatedAppointment);

            this.logger.info("Appointment updated with id: " + appointmentDTO.getId());
            return appointmentDTO;
        } catch (RuntimeException e) {
            this.logger.warning("Could not update appointment with id: " + dto.getId());
            throw e;
        }
    }

    public void deleteAppointmentById(int id) throws RuntimeException {
        this.logger.info("Deleting appointment with id: " + id);

        try {
            this.appointmentsGateway.deleteAppointment(id);
            this.logger.info("Appointment deleted with id: " + id);
        } catch (RuntimeException e) {
            this.logger.warning("Could not delete appointment with id: " + id);
            throw e;
        }
    }

    public void cascadeDeletePatientAppointments(int patientId) throws RuntimeException {
        this.logger.info("Deleting all appointments for patient with id: " + patientId);

        try {
            this.appointmentsGateway.cascadeDeletePatientAppointments(patientId);
            this.logger.info("All appointments for patient with id: " + patientId + " deleted");
        } catch (RuntimeException e) {
            this.logger.warning("Could not delete all appointments for patient with id: " + patientId);
            throw e;
        }
    }

    public void cascadeDeleteDoctorAppointments(int doctorId) throws RuntimeException {
        this.logger.info("Deleting all appointments for doctor with id: " + doctorId);

        try {
            this.appointmentsGateway.cascadeDeleteDoctorAppointments(doctorId);
            this.logger.info("All appointments for doctor with id: " + doctorId + " deleted");
        } catch (RuntimeException e) {
            this.logger.warning("Could not delete all appointments for doctor with id: " + doctorId);
            throw e;
        }
    }
    public SickLeaveDTO createSickLeave(CreateSickLeaveDTO dto) throws RuntimeException {
        this.logger.info("Creating new Sick Leave entity");

        Appointments.CreateSickLeaveRequest request = Appointments.CreateSickLeaveRequest.newBuilder()
            .setDate(dto.getDate())
            .setDaysOfLeave(dto.getNumberOfDays())
            .build();

        try {
            Appointments.SickLeave sickLeave = this.appointmentsGateway.createSickLeave(request);
            SickLeaveDTO sickLeaveDto = Utils.getDtoFromSickLeaveGrpc(sickLeave);

            this.logger.info("Created Sick Leave entity with id: " + sickLeaveDto.getId());
            return sickLeaveDto;
        } catch (RuntimeException e) {
            this.logger.warning("Could not create new Sick Leave entity");
            throw e;
        }
    }

    public SickLeaveDTO getSickLeaveById(int id) throws RuntimeException {
        this.logger.info("Retrieving Sick Leave entity with id: " + id);

        try {
            Appointments.SickLeave sickLeave = this.appointmentsGateway.getSickLeaveById(id);
            SickLeaveDTO sickLeaveDTO = Utils.getDtoFromSickLeaveGrpc(sickLeave);

            this.logger.info("Retrieved Sick Leave entity with id: " + id);
            return sickLeaveDTO;
        } catch (RuntimeException e) {
            this.logger.info("Error retrieving Sick Leave entity: " + e.getMessage());
            throw e;
        }
    }

    public List<SickLeaveDTO> getAllSickLeaveEntities() throws RuntimeException {
        this.logger.info("Retrieving all doctors");

        try {
            Appointments.SickLeaveEntitiesList entities = this.appointmentsGateway.getAllSickLeaveEntities();
            List<SickLeaveDTO> sickLeaveDtos = new ArrayList<>();

            entities.getSickLeaveEntitiesList().forEach(entity -> {
                sickLeaveDtos.add(Utils.getDtoFromSickLeaveGrpc(entity));
            });

            this.logger.info("Retrieved " + sickLeaveDtos.size() + " Sick Leave entities");
            return sickLeaveDtos;
        } catch (RuntimeException e) {
            this.logger.info("Error retrieving Sick Leave entities: " + e.getMessage());
            throw e;
        }
    }

    public SickLeaveDTO updateSickLeave(UpdateSickLeaveDTO dto) throws RuntimeException {
        this.logger.info("Updating sick leave entity with id " + dto.getId());

        try {
            Appointments.SickLeave sickLeave = this.appointmentsGateway.updateSickLeave(dto.safeConvertToGrpcRequest());
            SickLeaveDTO sickLeaveDto = Utils.getDtoFromSickLeaveGrpc(sickLeave);

            this.logger.info("Successfully update sick leave with id: " + sickLeaveDto.getId());
            return sickLeaveDto;
        } catch (RuntimeException e) {
            this.logger.info("Error updating Sick Leave: " + e.getMessage());
            throw e;
        }
    }

    public void deleteSickLeaveById(int id) throws RuntimeException {
        this.logger.info("Deleting Sick Leave entity with id: " + id);

        try {
            this.appointmentsGateway.deleteSickLeave(id);
            this.logger.info("Deleted Sick Leave entity with id: " + id);
        } catch (RuntimeException e) {
            this.logger.info("Error deleting sick leave entity: " + e.getMessage());
            throw e;
        }
    }

    public IcdDTO createIcd(CreateIcdDTO dto) throws RuntimeException {
        this.logger.info("Creating new ICD");

        Appointments.CreateIcdRequest request = Appointments.CreateIcdRequest.newBuilder()
            .setCode(dto.getCode())
            .setDescription(dto.getDescription())
            .build();

        try {
            Appointments.Icd icd = this.appointmentsGateway.createIcd(request);
            IcdDTO icdDto = Utils.getDtoFromIcdGrpc(icd);

            this.logger.info("Created ICD with id: " + icdDto.getId());
            return icdDto;
        } catch (RuntimeException e) {
            this.logger.warning("Could not create new ICD");
            throw e;
        }
    }

    public IcdDTO getIcdById(int id) throws RuntimeException {
        this.logger.info("Retrieving ICD with id: " + id);

        try {
            Appointments.Icd icd = this.appointmentsGateway.getIcdById(id);
            IcdDTO icdDto = Utils.getDtoFromIcdGrpc(icd);

            this.logger.info("Retrieved ICD with id: " + id);
            return icdDto;
        } catch (RuntimeException e) {
            this.logger.warning("Could not retrieve ICD with id: " + id);
            throw e;
        }
    }

    public List<IcdDTO> getAllIcds() throws RuntimeException {
        this.logger.info("Retrieving all ICDs");

        try {
            Appointments.IcdEntitiesList icds = this.appointmentsGateway.getAllIcdEntities();
            List<IcdDTO> icdDtos = new ArrayList<>();

            icds.getIcdEntitiesList().forEach(icd -> {
                icdDtos.add(Utils.getDtoFromIcdGrpc(icd));
            });

            this.logger.info("Retrieved " + icdDtos.size() + " ICDs");
            return icdDtos;
        } catch (RuntimeException e) {
            this.logger.warning("Could not retrieve all ICDs");
            throw e;
        }
    }

    public IcdDTO updateIcd(UpdateIcdDTO dto) throws RuntimeException {
        this.logger.info("Updating ICD with id " + dto.getId());

        try {
            Appointments.Icd icd = this.appointmentsGateway.updateIcd(dto.safeConvertToGrpcRequest());
            IcdDTO icdDto = Utils.getDtoFromIcdGrpc(icd);

            this.logger.info("Successfully updated ICD with id: " + icdDto.getId());
            return icdDto;
        } catch (RuntimeException e) {
            this.logger.warning("Could not update ICD with id: " + dto.getId());
            throw e;
        }
    }

    public void deleteIcdById(int id) throws RuntimeException {
        this.logger.info("Deleting ICD with id: " + id);

        try {
            this.appointmentsGateway.deleteIcd(id);
            this.logger.info("Deleted ICD with id: " + id);
        } catch (RuntimeException e) {
            this.logger.warning("Could not delete ICD with id: " + id);
            throw e;
        }
    }

    public DiagnosisDTO createDiagnosis(CreateDiagnosisDTO dto) throws RuntimeException {
        this.logger.info("Creating new diagnosis");

        try {
            Appointments.Diagnosis diagnosis = this.appointmentsGateway.createDiagnosis(dto.safeConvertToGrpcRequest());
            DiagnosisDTO diagnosisDto = Utils.getDtoFromDiagnosisGrpc(diagnosis);

            this.logger.info("Created diagnosis with id: " + diagnosisDto.getId());
            return diagnosisDto;
        } catch (RuntimeException e) {
            this.logger.warning("Could not create new diagnosis");
            throw e;
        }
    }

    public DiagnosisDTO getDiagnosisById(int id) throws RuntimeException {
        this.logger.info("Retrieving diagnosis by id: " + id);

        try {
            Appointments.Diagnosis diagnosis = this.appointmentsGateway.getDiagnosisById(id);
            DiagnosisDTO diagnosisDto = Utils.getDtoFromDiagnosisGrpc(diagnosis);

            this.logger.info("Retrieved diagnosis with id: " + diagnosisDto.getId());
            return diagnosisDto;
        } catch (RuntimeException e) {
            this.logger.warning("Could not retrieve diagnosis with id: " + id);
            throw e;
        }
    }

    public List<DiagnosisDTO> getAllDiagnoses() throws RuntimeException {
        this.logger.info("Retrieving all diagnoses");

        try {
            Appointments.DiagnosesList diagnoses = this.appointmentsGateway.getAllDiagnoses();
            List<DiagnosisDTO> diagnosesDtos = new ArrayList<>();

            diagnoses.getDiagnosesList().forEach(diagnosis -> {
                diagnosesDtos.add(Utils.getDtoFromDiagnosisGrpc(diagnosis));
            });

            this.logger.info("Retrieved " + diagnosesDtos.size() + " diagnoses");
            return diagnosesDtos;
        } catch (RuntimeException e) {
            this.logger.warning("Could not retrieve all diagnoses: " + e.getMessage());
            throw e;
        }
    }

    public DiagnosisDTO updateDiagnosis(UpdateDiagnosisDTO dto) throws RuntimeException {
        this.logger.info("Updating diagnosis with id: " + dto.getId());

        try {
            Appointments.Diagnosis diagnosis = this.appointmentsGateway.updateDiagnosis(dto.safeConvertToGrpcRequest());
            DiagnosisDTO diagnosisDto = Utils.getDtoFromDiagnosisGrpc(diagnosis);

            this.logger.info("Successfully updated diagnosis with id: " + diagnosisDto.getId());
            return diagnosisDto;
        } catch (RuntimeException e) {
            this.logger.warning("Could not update diagnosis: " + e.getMessage());
            throw e;
        }
    }

    public void deleteDiagnosisById(int id) throws RuntimeException {
        this.logger.info("Deleting diagnosis with id: " + id);

        try {
            this.appointmentsGateway.deleteDiagnosis(id);
            this.logger.info("Deleted diagnosis with id: " + id);
        } catch (RuntimeException e) {
            this.logger.warning("Could not delete diagnosis with id: " + id);
            throw e;
        }
    }

    public List<IcdDTO> startAppointmentFetchIcds(StartAppointmentDTO dto) throws RuntimeException {
        int appointmentId = dto.getAppointmentId();
        int doctorId = dto.getDoctorId();

        this.logger.info("Starting appointment with id: " + appointmentId);

        try {
            Appointments.StartAppointmentFetchIcdsRequest request = Appointments.StartAppointmentFetchIcdsRequest.newBuilder()
                .setAppointmentId(appointmentId)
                .setDoctorId(doctorId)
                .build();

            Appointments.IcdEntitiesList icdEntitiesList = this.appointmentsGateway.startAppointmentFetchIcds(request);

            List<IcdDTO> icdDtos = new ArrayList<>();
            icdEntitiesList.getIcdEntitiesList().forEach(icd -> {
                icdDtos.add(Utils.getDtoFromIcdGrpc(icd));
            });

            this.logger.info("Retrieved " + icdDtos.size() + " icds");
            return icdDtos;
        } catch (RuntimeException e) {
            this.logger.warning("Could not start appointment with id: " + appointmentId);
            throw e;
        }
    }

    public void finishAppointmentAddDiagnosis(FinishAppointmentDTO dto) throws RuntimeException {
        int appointmentId = dto.getAppointmentId();
        this.logger.info("Finishing appointment with id: " + appointmentId);

        try {
            CreateDiagnosisDTO createDiagnosisDto = new CreateDiagnosisDTO(
                dto.getTreatmentDescription(),
                dto.getIcdId(),
                dto.getSickLeaveDate(),
                dto.getSickLeaveDays()
            );
            Appointments.FinishAppointmentAddDiagnosisRequest request = Appointments.FinishAppointmentAddDiagnosisRequest.newBuilder()
                .setAppointmentId(appointmentId)
                .setDiagnosis(createDiagnosisDto.safeConvertToGrpcRequest())
                .build();

            this.appointmentsGateway.finishAppointmentAddDiagnosis(request);
        } catch (RuntimeException e) {
            this.logger.warning("Could not finish appointment with id: " + appointmentId);
            throw e;
        }
    }

    public List<Integer> getAllPatientIdsForIcd(int icdId) throws RuntimeException {
        this.logger.info("Retrieving all patient ids for icd: " + icdId);

        try {
            Appointments.IdsList idsList = this.appointmentsGateway.getAllPatientIdsForIcd(Int32Value.of(icdId));
            List<Integer> patientIds = new ArrayList<>(idsList.getIdList());
            this.logger.info("Retrieved " + patientIds.size() + " patient IDs");
            return patientIds;
        } catch (RuntimeException e) {
            this.logger.warning("Could not retrieve all patient ids for icd: " + icdId);
            throw e;
        }
    }

    public List<IcdOccurrenceDTO> mostFrequentIcds(int limit) throws RuntimeException {
        this.logger.info("Retrieving most frequent ICDs with limit: " + limit);

        try {
            Appointments.IcdOccurrenceList list = this.appointmentsGateway.mostFrequentIcds(Int32Value.of(limit));
            List<IcdOccurrenceDTO> icdOccurrenceDTOs = new ArrayList<>();

            list.getIcdOccurrenceList().forEach(icdOccurrence -> {
                icdOccurrenceDTOs.add(Utils.getOccurrenceDtoFromIcdGrpc(icdOccurrence));
            });

            this.logger.info("Retrieved " + icdOccurrenceDTOs.size() + " icds");
            return icdOccurrenceDTOs;
        } catch (RuntimeException e) {
            this.logger.warning("Could not retrieve most frequent ICDs");
            throw e;
        }
    }

    public List<DoctorAppointmentsCountDTO> getDoctorAppointmentsCount() throws RuntimeException {
        this.logger.info("Retrieving doctor appointments count");

        try {
            Appointments.DoctorAppointmentsCountList list = this.appointmentsGateway.getDoctorAppointmentsCount();
            List<DoctorAppointmentsCountDTO> doctorAppointmentsCountDTOs = new ArrayList<>();

            list.getCountList().forEach(grpcModel -> {
                doctorAppointmentsCountDTOs.add(Utils.getDoctorAppointmentsCountFromGrpc(grpcModel));
            });

            this.logger.info("Retrieved doctor appointments count: " + doctorAppointmentsCountDTOs.size());
            return doctorAppointmentsCountDTOs;
        } catch (RuntimeException e) {
            this.logger.warning("Could not retrieve doctor appointments count");
            throw e;
        }
    }

    public List<AppointmentsByPatientDTO> getAppointmentsByPatient() throws RuntimeException {
        this.logger.info("Retrieving appointment by patient");

        try {
            Appointments.AppointmentsByPatientList list = this.appointmentsGateway.getAppointmentsByPatientList();
            List<AppointmentsByPatientDTO> appointmentsByPatientDTOs = new ArrayList<>();

            list.getListList().forEach(grpcModel -> {
                appointmentsByPatientDTOs.add(Utils.getAppointmentsByPatientFromGrpc(grpcModel));
            });

            this.logger.info("Retrieved appointments by patient: " + appointmentsByPatientDTOs.size());
            return appointmentsByPatientDTOs;
        } catch (RuntimeException e) {
            this.logger.warning("Could not retrieve appointments by patient");
            throw e;
        }
    }

    public List<AppointmentDTO> getAppointmentsForTimePeriod(
        String startDate,
        String endDate,
        Optional<Integer> doctorId
    ) throws RuntimeException {
        this.logger.info("Retrieving appointment by doctor " + doctorId + "for time period: " + startDate + " - " + endDate);

        try {
            Appointments.ListAppointmentsForTimePeriodRequest.Builder builder = Appointments.ListAppointmentsForTimePeriodRequest.newBuilder();
            builder.setStartDate(startDate);
            builder.setEndDate(endDate);
            doctorId.ifPresent(builder::setDoctorId);

            Appointments.AppointmentsList list = this.appointmentsGateway.getAppointmentsForTimePeriod(builder.build());

            List<AppointmentDTO> appointmentDTOs = new ArrayList<>();
            list.getAppointmentsList().forEach(grpcModel -> {
                appointmentDTOs.add(Utils.getDTOFromAppointmentsGrpc(grpcModel));
            });

            this.logger.info(String.format("Retrieved %d appointments for period: %s - %s ", appointmentDTOs.size(), startDate, endDate));
            return appointmentDTOs;
        } catch (RuntimeException e) {
            this.logger.warning("Could not retrieve appointments for time period: " + startDate + " - " + endDate);
            throw e;
        }
    }

    public MonthWithMostSickLeavesDTO getMonthWithMostSickLeaves(String year) throws RuntimeException {
        this.logger.info("Retrieving month with most sick leaves for year: " + year);

        try {
            Appointments.MostSickLeavesRequest request = Appointments.MostSickLeavesRequest.newBuilder().setYear(year).build();
            Appointments.MonthWithMostSickLeaves grpcModel = this.appointmentsGateway.getMonthWithMostSickLeaves(request);

            this.logger.info("Successfully retrieved the month with most sick leaves for year: " + year);
            return Utils.getMonthWithMostSickLeavesFromGrpc(grpcModel);
        } catch (RuntimeException e) {
            this.logger.warning("Could not retrieve month with most sick leaves for year: " + year);
            throw e;
        }
    }

    public List<DoctorSickLeaveCountDTO> getDoctorsBySickLeaveCount(int limit) throws RuntimeException {
        this.logger.info("Retrieving doctors with most sick leaves with limit: " + limit);

        try {
            Appointments.DoctorsBySickLeaveCountRequest request = Appointments.DoctorsBySickLeaveCountRequest.newBuilder()
                .setLimit(limit)
                .build();

            Appointments.DoctorsWithMostSickLeavesList grpcList = this.appointmentsGateway.getDoctorsBySickLeaveCount(request);

            List<DoctorSickLeaveCountDTO> dtos = new ArrayList<>();
            grpcList.getDoctorWithMostSickLeavesList().forEach(grpcModel -> {
                dtos.add(Utils.getDoctorsBySickLeaveCount(grpcModel));
            });

            this.logger.info("Successfully retrieved most sick leaves by doctors with limit: " + limit);
            return dtos;
        } catch (RuntimeException e) {
            this.logger.info("Could not retrieve sick leaves by doctors with limit: " + limit);
            throw e;
        }
    }
}
