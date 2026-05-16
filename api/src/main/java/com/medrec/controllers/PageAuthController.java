package com.medrec.controllers;

import com.medrec.annotations.AuthGuard;
import com.medrec.utils.HTTPResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("api/pages")
public class PageAuthController {
    private final Logger logger = Logger.getLogger(AppointmentsController.class.getName());

    @RequestMapping("/login")
    public ResponseEntity<HTTPResponse> login() {
        this.logger.info("Accessing login page");
        HTTPResponse response = new HTTPResponse(
            "SUCCESS",
            "Login page is accessible to user"
        );
        return ResponseEntity.ok(response);
    }

    @RequestMapping("/register")
    public ResponseEntity<HTTPResponse> register() {
        this.logger.info("Accessing register page");
        HTTPResponse response = new HTTPResponse(
            "SUCCESS",
            "Register page is accessible to user"
        );
        return ResponseEntity.ok(response);
    }

    @AuthGuard({"admin"})
    @RequestMapping("/dashboard")
    public ResponseEntity<HTTPResponse> adminDashboard() {
        this.logger.info("Accessing dashboard page");
        HTTPResponse response = new HTTPResponse(
            "SUCCESS",
            "Dashboard page is accessible to user"
        );
        return ResponseEntity.ok(response);
    }

    @AuthGuard({"doctor", "patient"})
    @RequestMapping("/menu")
    public ResponseEntity<HTTPResponse> menu() {
        this.logger.info("Accessing menu page");
        HTTPResponse response = new HTTPResponse(
            "SUCCESS",
            "Menu page is accessible to user"
        );
        return ResponseEntity.ok(response);
    }

    @RequestMapping("/home")
    public ResponseEntity<HTTPResponse> home() {
        this.logger.info("Accessing home page");
        HTTPResponse response = new HTTPResponse(
            "SUCCESS",
            "Home page is accessible to user"
        );
        return ResponseEntity.ok(response);
    }

    @AuthGuard({"admin"})
    @RequestMapping("/admin-query")
    public ResponseEntity<HTTPResponse> adminQuery() {
        this.logger.info("Accessing admin query page");
        HTTPResponse response = new HTTPResponse(
            "SUCCESS",
            "Admin Query page is accessible to user"
        );
        return ResponseEntity.ok(response);
    }
}
