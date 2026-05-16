package com.medrec.controllers;

import com.medrec.annotations.AuthGuard;
import com.medrec.dtos.appointments.appointment.*;
import com.medrec.dtos.appointments.icd.IcdOccurrenceDTO;
import com.medrec.dtos.queries.PatientCountDTO;
import com.medrec.dtos.users.patient.PatientDTO;
import com.medrec.services.AppointmentsService;
import com.medrec.services.GeneralWorkService;
import com.medrec.services.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("api/query")
public class QueryController {
    private final Logger logger = Logger.getLogger(QueryController.class.getName());
    private final UsersService usersService;
    private final GeneralWorkService generalWorkService;
    private final AppointmentsService appointmentsService;

    public QueryController(UsersService usersService, GeneralWorkService generalWorkService, AppointmentsService appointmentsService) {
        this.usersService = usersService;
        this.generalWorkService = generalWorkService;
        this.appointmentsService = appointmentsService;
    }

    @AuthGuard({"admin"})
    @GetMapping("get-all-patients-for-gp/{id}")
    public ResponseEntity<List<PatientDTO>> getAllPatientsByGpId(@PathVariable("id") Integer id) {
        this.logger.info("Called endpoint Get All Patients for GP id " + id);
        if (id == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(this.usersService.getAllPatientsByGpId(id));
    }

    @AuthGuard({"admin"})
    @GetMapping("get-patients-count-for-gp-doctors")
    public ResponseEntity<List<PatientCountDTO>> countOfPatientsForDoctors() {
        this.logger.info("Called endpoint Get Patients Count For All GP Doctors");
        return ResponseEntity.ok(this.usersService.countOfPatientsForDoctors());
    }

    @AuthGuard({"admin"})
    @GetMapping("get-patients-by-icd/{id}")
    public ResponseEntity<List<PatientDTO>> getPatientsByIcd(@PathVariable("id") Integer id) {
        this.logger.info("Called endpoint Get Patients By ICD id " + id);
        if (id == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(this.generalWorkService.pipelinePatientsFromIcdId(id));
    }

    @AuthGuard({"admin"})
    @GetMapping("get-most-frequent-icds/{limit}")
    public ResponseEntity<List<IcdOccurrenceDTO>> getMostFrequentIcds(@PathVariable("limit") Integer limit) {
        this.logger.info("Called endpoint Get Most Frequent Icd Occurrences With Limit");
        if (limit == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(this.appointmentsService.mostFrequentIcds(limit));
    }

    @AuthGuard({"admin"})
    @GetMapping("get-doctor-appointments-count")
    public ResponseEntity<List<DoctorAppointmentsCountDTO>> getDoctorAppointmentsCount() {
        this.logger.info("Getting doctor appointments count endpoint called");
        return ResponseEntity.ok(this.appointmentsService.getDoctorAppointmentsCount());
    }

    @AuthGuard({"admin"})
    @GetMapping("list-appointments-by-patient")
    public ResponseEntity<List<AppointmentsByPatientDTO>> listAppointmentsByPatient() {
        this.logger.info("Called endpoint Get Appointments By Patient");
        return ResponseEntity.ok(this.appointmentsService.getAppointmentsByPatient());
    }

    @AuthGuard({"admin"})
    @GetMapping("list-appointments-for-time-period/{startDate}/{endDate}")
    public ResponseEntity<List<AppointmentDTO>> listAppointmentsForTimePeriod(
        @PathVariable("startDate") String startDate,
        @PathVariable("endDate") String endDate,
        @RequestParam(name = "doctorId", required = false) Integer doctorId
    ) {
        this.logger.info("Called endpoint Get Appointments For Time Period: " + startDate + " " + endDate);

        if (startDate == null || endDate == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(this.appointmentsService.getAppointmentsForTimePeriod(
            startDate,
            endDate,
            Optional.ofNullable(doctorId)
        ));
    }

    @AuthGuard({"admin"})
    @GetMapping("get-month-with-most-sick-leaves/{year}")
    public ResponseEntity<MonthWithMostSickLeavesDTO> getMonthWithMostSickLeaves(@PathVariable("year") String year) {
        this.logger.info("Called endpoint Get Month With Most Sick Leaves");

        if (year == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(this.appointmentsService.getMonthWithMostSickLeaves(year));
    }

    @AuthGuard({"admin"})
    @GetMapping("get-doctors-by-sick-leave-count/{limit}")
    public ResponseEntity<List<DoctorSickLeaveCountDTO>> getDoctorsBySickLeaveCount(@PathVariable("limit") Integer limit) {
        this.logger.info("Called endpoint Get Doctors By Sick Leave Count");

        if (limit == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(this.appointmentsService.getDoctorsBySickLeaveCount(limit));
    }
}
