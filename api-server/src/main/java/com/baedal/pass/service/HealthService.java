package com.baedal.pass.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baedal.pass.mapper.HealthMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthService {

    private final HealthMapper healthMapper;

    @Transactional(readOnly = true)
    public boolean checkHealth() {
        try {
            log.debug("Attempting to check database connection...");
            return healthMapper.checkDatabaseConnection() == 1;
        } catch (Exception e) {
            log.error("Database connection failed. Cause: {}", e.getMessage(), e);
            return false;
        }


    }

}
