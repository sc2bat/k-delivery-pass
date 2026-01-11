package com.baedal.pass.runner;

import com.baedal.pass.service.HealthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseConnectionTest implements CommandLineRunner {

    private final HealthService healthService;

    @Override
    public void run(String... args) {
        log.info("==========================================");
        log.info("[Startup] Checking Database Connection...");

        boolean isUp = healthService.checkHealth();

        if (isUp) {
            log.info("[Startup] Database Connection: STABLE (UP)");
            log.info("[Startup] Configuration verified. Ready to serve requests.");
        } else {
            log.error("[Startup] Database Connection: FAILED (DOWN)");
            log.error("[Startup] Please check your 'application.yml' or database server status.");
        }
        
        log.info("==========================================");
    }
}