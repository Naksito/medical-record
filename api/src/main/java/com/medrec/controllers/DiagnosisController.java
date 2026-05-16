package com.medrec.controllers;

import com.medrec.annotations.AuthGuard;
import com.medrec.dtos.appointments.diagnosis.CreateDiagnosisDTO;
import com.medrec.dtos.appointments.diagnosis.DiagnosisDTO;
import com.medrec.dtos.appointments.diagnosis.UpdateDiagnosisDTO;
import com.medrec.services.AppointmentsService;
import com.medrec.utils.HTTPResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("api/diagnosis")
public class DiagnosisController {
    private final Logger logger = Logger.getLogger(DiagnosisController.class.getName());
    private final AppointmentsService appointmentsService;

    public DiagnosisController(AppointmentsService appointmentsService) {
        this.appointmentsService = appointmentsService;
    }

    @AuthGuard({"admin"})
    @PostMapping("create")
    public ResponseEntity<DiagnosisDTO> createDiagnosis(@RequestBody CreateDiagnosisDTO dto) {
        this.logger.info("Creating new diagnosis endpoint called");
        return ResponseEntity.ok(this.appointmentsService.createDiagnosis(dto));
    }

    @AuthGuard({"admin"})
    @GetMapping("get/id")
    public ResponseEntity<DiagnosisDTO> getDiagnosisById(@RequestParam("id") int id) {
        this.logger.info("Getting diagnosis by id " + id + " endpoint called");
        return ResponseEntity.ok(this.appointmentsService.getDiagnosisById(id));
    }

    @AuthGuard({"admin"})
    @GetMapping("list/all")
    public ResponseEntity<List<DiagnosisDTO>> getAllDiagnosisEntities() {
        this.logger.info("List all diagnosis entities endpoint called");
        return ResponseEntity.ok(this.appointmentsService.getAllDiagnoses());
    }

    @AuthGuard({"admin"})
    @PutMapping("update")
    public ResponseEntity<DiagnosisDTO> updateDiagnosis(@RequestBody UpdateDiagnosisDTO dto) {
        this.logger.info("Update diagnosis endpoint called with id " + dto.getId());
        return ResponseEntity.ok(this.appointmentsService.updateDiagnosis(dto));
    }

    @AuthGuard({"admin"})
    @DeleteMapping("delete")
    public ResponseEntity<HTTPResponse> deleteDiagnosisById(@RequestParam("id") int id) {
        this.logger.info("Deleting diagnosis by id " + id + "endpoint called");
        this.appointmentsService.deleteDiagnosisById(id);
        HTTPResponse response = new HTTPResponse("SUCCESS", "Diagnosis deleted successfully");
        return ResponseEntity.ok(response);
    }
}
