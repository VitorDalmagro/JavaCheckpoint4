package com.cp4.TechAlert.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public ResponseEntity<Map<String, String>> health(){
        Map<String, String> status = new LinkedHashMap<>();
        status.put("status", "UP");
        status.put("application", "TechAlert");
        return ResponseEntity.ok(status);
    }
}