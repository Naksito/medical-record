package com.medrec.controllers;

import com.medrec.annotations.AuthGuard;
import com.medrec.dtos.appointments.sick_leave.CreateSickLeaveDTO;
import com.medrec.dtos.appointments.sick_leave.SickLeaveDTO;
import com.medrec.dtos.appointments.sick_leave.UpdateSickLeaveDTO;
import com.medrec.services.AppointmentsService;
import com.medrec.utils.HTTPResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("api/sick-leave")
public class SickLeaveController {
    private final Logger logger = Logger.getLogger(SickLeaveController.class.getName());
    private final AppointmentsService appointmentsService;

    @Autowired
    public SickLeaveController(AppointmentsService appointmentsService) {
        this.appointmentsService = appointmentsService;
    }

    @AuthGuard({"admin"})
    @PostMapping("create")
    public ResponseEntity<SickLeaveDTO> createSickLeave(@RequestBody CreateSickLeaveDTO dto) {
        this.logger.info("Creating new sick leave");
        return ResponseEntity.ok(this.appointmentsService.createSickLeave(dto));
    }

    @AuthGuard({"admin"})
    @GetMapping("get/id")
    public ResponseEntity<SickLeaveDTO> getSickLeaveById(@RequestParam("id") int id) {
        this.logger.info("Get sick leave by id " + id + " endpoint called");
        return ResponseEntity.ok(this.appointmentsService.getSickLeaveById(id));
    }

    @AuthGuard({"admin"})
    @GetMapping("list/all")
    public ResponseEntity<List<SickLeaveDTO>> getAllSickLeaveEntities() {
        this.logger.info("Get all sick leave entities endpoint called");
        return ResponseEntity.ok(this.appointmentsService.getAllSickLeaveEntities());
    }

    @AuthGuard({"admin"})
    @PutMapping("update")
    public ResponseEntity<SickLeaveDTO> updateSickLeave(@RequestBody UpdateSickLeaveDTO dto) {
        this.logger.info("Updating sick leave endpoint called");
        return ResponseEntity.ok(this.appointmentsService.updateSickLeave(dto));
    }

    @AuthGuard({"admin"})
    @DeleteMapping("delete")
    public ResponseEntity<HTTPResponse> deleteSickLeaveById(@RequestParam("id") int id) {
        this.logger.info("Deleting sick leave by id " + id + "endpoint called");
        this.appointmentsService.deleteSickLeaveById(id);
        HTTPResponse response = new HTTPResponse("SUCCESS", "Sick Leave deleted successfully");
        return ResponseEntity.ok(response);
    }
}
