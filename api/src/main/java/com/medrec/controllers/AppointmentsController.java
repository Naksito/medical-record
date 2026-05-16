package com.medrec.controllers;

import com.medrec.annotations.AuthGuard;
import com.medrec.dtos.appointments.appointment.*;
import com.medrec.dtos.appointments.icd.IcdDTO;
import com.medrec.services.AppointmentsService;
import com.medrec.utils.HTTPResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("api/appointments")
public class AppointmentsController {
    private final Logger logger = Logger.getLogger(AppointmentsController.class.getName());
    private final AppointmentsService appointmentsService;

    @Autowired
    public AppointmentsController(AppointmentsService appointmentsService) {
        this.appointmentsService = appointmentsService;
    }

    @AuthGuard({"patient", "admin"})
    @PostMapping("create")
    public ResponseEntity<AppointmentDTO> createAppointment(@RequestBody CreateAppointmentDTO dto) {
        this.logger.info("Creating new appointment endpoint called");
        return ResponseEntity.ok(this.appointmentsService.createAppointment(dto));
    }

    @AuthGuard({"doctor", "admin"})
    @GetMapping("get/id")
    public ResponseEntity<AppointmentDTO> getAppointmentById(@RequestParam("id") int id) {
        this.logger.info(String.format("Searching for appointment by id %s endpoint called", id));
        return ResponseEntity.ok(this.appointmentsService.getAppointmentById(id));
    }

    @AuthGuard({"admin"})
    @GetMapping("list/all")
    public ResponseEntity<List<AppointmentDTO>> listAllAppointments() {
        this.logger.info("List all appointments endpoint called");
        return ResponseEntity.ok(this.appointmentsService.getAllAppointments());
    }

    @AuthGuard({"patient", "admin"})
    @GetMapping("list/all/patient-email")
    public ResponseEntity<List<AppointmentDTO>> listAllByPatientEmail(@RequestParam("email") String email) {
        this.logger.info(String.format("List all appointments by patient email %s endpoint called", email));
        return ResponseEntity.ok(this.appointmentsService.getAllByPatientEmail(email));
    }

    @AuthGuard({"patient", "admin"})
    @GetMapping("list/all/patient-id")
    public ResponseEntity<List<AppointmentDTO>> listAllByPatientEmail(@RequestParam("id") int id) {
        this.logger.info(String.format("List all appointments by patient id %s endpoint called", id));
        return ResponseEntity.ok(this.appointmentsService.getAllByPatientId(id));
    }

    @AuthGuard({"doctor", "admin"})
    @GetMapping("list/all/doctor-email")
    public ResponseEntity<List<AppointmentDTO>> listAllByDoctorEmail(@RequestParam("email") String email) {
        this.logger.info(String.format("List all appointments by doctor email %s endpoint called", email));
        return ResponseEntity.ok(this.appointmentsService.getAllByDoctorEmail(email));
    }

    @AuthGuard({"doctor", "admin"})
    @GetMapping("list/all/doctor-id")
    public ResponseEntity<List<AppointmentDTO>> listAllByDoctorEmail(@RequestParam("id") int id) {
        this.logger.info(String.format("List all appointments by doctor id %s endpoint called", id));
        return ResponseEntity.ok(this.appointmentsService.getAllByDoctorId(id));
    }

    @AuthGuard({"admin"})
    @PutMapping("update")
    public ResponseEntity<AppointmentDTO> updateAppointment(@RequestBody UpdateAppointmentDTO dto) {
        this.logger.info(String.format("Updating appointment with id %s endpoint called", dto.getId()));
        return ResponseEntity.ok(this.appointmentsService.updateAppointment(dto));
    }

    @AuthGuard({"admin"})
    @DeleteMapping("delete")
    public ResponseEntity<HTTPResponse> deleteDoctorById(@RequestParam("id") int id) {
        this.logger.info("Deleting appointment by id " + id + "endpoint called");
        this.appointmentsService.deleteAppointmentById(id);
        HTTPResponse response = new HTTPResponse("SUCCESS", "Appointment deleted successfully");
        return ResponseEntity.ok(response);
    }

    @AuthGuard({"doctor", "admin"})
    @PostMapping("start")
    public ResponseEntity<List<IcdDTO>> startAppointment(@RequestBody StartAppointmentDTO dto) {
        this.logger.info("Starting appointment endpoint called");
        return ResponseEntity.ok(this.appointmentsService.startAppointmentFetchIcds(dto));
    }

    @AuthGuard({"doctor", "admin"})
    @PostMapping("finish")
    public ResponseEntity<HTTPResponse> finishAppointment(@RequestBody FinishAppointmentDTO dto) {
        this.logger.info("Finishing appointment endpoint called");
        this.appointmentsService.finishAppointmentAddDiagnosis(dto);
        HTTPResponse response = new HTTPResponse("SUCCESS", "Appointment deleted successfully");
        return ResponseEntity.ok(response);
    }
}
