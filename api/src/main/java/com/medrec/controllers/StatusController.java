package com.medrec.controllers;

import com.medrec.gateways.AppointmentsGateway;
import com.medrec.gateways.AuthGateway;
import com.medrec.gateways.UsersGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("api/status")
public class StatusController {
    private final Logger logger = Logger.getLogger(StatusController.class.getName());

    private final UsersGateway usersGateway;
    private final AuthGateway authGateway;
    private final AppointmentsGateway appointmentsGateway;

    @Autowired
    public StatusController(
        UsersGateway usersGateway,
        AuthGateway authGateway,
        AppointmentsGateway appointmentsGateway
    ) {
        this.usersGateway = usersGateway;
        this.authGateway = authGateway;
        this.appointmentsGateway = appointmentsGateway;
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> getStatus() {
        this.logger.info("Status endpoint called");

        String apiVersion = System.getenv("SERVICE_VERSION");
        if (apiVersion == null || apiVersion.isBlank()) {
            apiVersion = "unknown";
        }

        Map<String, String> versions = new LinkedHashMap<>();
        versions.put("api", apiVersion);
        versions.put("auth", authGateway.getVersion());
        versions.put("users", usersGateway.getVersion());
        versions.put("appointments", appointmentsGateway.getVersion());

        return ResponseEntity.ok(versions);
    }
}
