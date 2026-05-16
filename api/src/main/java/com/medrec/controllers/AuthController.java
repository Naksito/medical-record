package com.medrec.controllers;

import com.medrec.dtos.auth.AuthResponseDTO;
import com.medrec.dtos.auth.LogUserInDTO;
import com.medrec.dtos.auth.TokenRequestDTO;
import com.medrec.dtos.users.doctor.RegisterDoctorDTO;
import com.medrec.dtos.users.patient.RegisterPatientDTO;
import com.medrec.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    private final Logger logger = Logger.getLogger(AuthController.class.getName());
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("register-doctor")
    public ResponseEntity<AuthResponseDTO> registerDoctor(@RequestBody RegisterDoctorDTO dto) {
        this.logger.info("Registering new doctor");
        return ResponseEntity.ok(this.authService.registerDoctor(dto));
    }

    @PostMapping("register-patient")
    public ResponseEntity<AuthResponseDTO> registerPatient(@RequestBody RegisterPatientDTO dto) {
        this.logger.info("Registering new patient");
        return ResponseEntity.ok(this.authService.registerPatient(dto));
    }

    @PostMapping("log-doctor-in")
    public ResponseEntity<AuthResponseDTO> logDoctorIn(@RequestBody LogUserInDTO dto) {
        this.logger.info("Trying to fulfil log doctor in request");
        return ResponseEntity.ok(this.authService.logDoctorIn(dto));
    }

    @PostMapping("log-patient-in")
    public ResponseEntity<AuthResponseDTO> logPatientIn(@RequestBody LogUserInDTO dto) {
        this.logger.info("Trying to fulfil log patient in request");
        return ResponseEntity.ok(this.authService.logPatientIn(dto));
    }

    @PostMapping("log-admin-in")
    public ResponseEntity<AuthResponseDTO> logAdminIn(@RequestBody LogUserInDTO dto) {
        this.logger.info("Trying to fulfil log admin in request");
        return ResponseEntity.ok(this.authService.logAdminIn(dto));
    }

    @GetMapping("is-request-authorized")
    public ResponseEntity<Boolean> isRequestAuthorized(@RequestBody TokenRequestDTO dto){
        this.logger.info("Checking if request is authorized");
        return ResponseEntity.ok(this.authService.isRequestAuthorized(dto.getToken(), dto.getRoleAsList()));
    }

    @GetMapping("validate-token")
    public ResponseEntity<AuthResponseDTO> isTokenValid(@RequestParam("token") String token){
        this.logger.info("Validating token");
        return ResponseEntity.ok(this.authService.isTokenValid(token));
    }
}
