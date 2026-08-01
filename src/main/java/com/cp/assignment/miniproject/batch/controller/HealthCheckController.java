package com.cp.assignment.miniproject.batch.controller;


import com.cp.assignment.miniproject.batch.model.MessageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller//for Web MVC (fronted)
public class HealthCheckController {
    @GetMapping("/")
    public String index() {
        return "index";
    }
    // API Health Check
    @GetMapping("/healthcheck")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(new MessageResponse("This is the health-check page : batch-service"));
    }


}
