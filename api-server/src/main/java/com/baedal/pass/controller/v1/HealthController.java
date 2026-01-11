package com.baedal.pass.controller.v1;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baedal.pass.service.HealthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@RestController
@RequiredArgsConstructor
public class HealthController {

    private final HealthService healthService;
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> checkHealth() {
        log.info("[HealthCheck] Request received.");
        Map<String, Object> response = new HashMap<>();

        boolean isUp = healthService.checkHealth();

        if(isUp){
            response.put("status", "UP");
            response.put("db", "CONNECTION");
            log.info("[HealthCheck] Status: UP");
            return ResponseEntity.ok(response);
        }else{
            response.put("status", "DOWN");
            response.put("db", "DISCONNECTION");
            log.info("[HealthCheck] Status: DOWN");
            return ResponseEntity.internalServerError().body(response);
        }
    }

}
