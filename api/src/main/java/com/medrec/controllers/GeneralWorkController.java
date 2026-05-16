package com.medrec.controllers;

import com.medrec.annotations.AuthGuard;
import com.medrec.dtos.general.DoctorMenuDataDTO;
import com.medrec.dtos.general.PatientMenuDataDTO;
import com.medrec.services.GeneralWorkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("api/general-work")
public class GeneralWorkController {
    private final Logger logger = Logger.getLogger(GeneralWorkController.class.getName());
    private final GeneralWorkService generalWorkService;

    public GeneralWorkController(GeneralWorkService generalWorkService) {
        this.generalWorkService = generalWorkService;
    }

    @AuthGuard({"patient", "admin"})
    @GetMapping("patient-menu-data")
    public ResponseEntity<PatientMenuDataDTO> getPatientMenuData(@RequestParam("id") int id) {
        this.logger.info("Get data for patient menu endpoint called for id: " + id);
        return  ResponseEntity.ok(generalWorkService.getPatientMenuData(id));
    }

    @AuthGuard({"doctor", "admin"})
    @GetMapping("doctor-menu-data")
    public ResponseEntity<DoctorMenuDataDTO> getDoctorMenuData(@RequestParam("id") int id) {
        this.logger.info("Get data for doctor menu endpoint called for id: " + id);
        return  ResponseEntity.ok(generalWorkService.getDoctorMenuData(id));
    }
}
