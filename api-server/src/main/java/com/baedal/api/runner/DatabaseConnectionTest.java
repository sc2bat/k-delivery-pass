package com.baedal.api.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import javax.sql.DataSource;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseConnectionTest implements CommandLineRunner {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        log.info("==========================================");
        log.info("🔌 [DB 연결 테스트] 시작합니다...");

        try {
            try (Connection connection = dataSource.getConnection()) {
                log.info("DB 접속 성공! (URL: {})", connection.getMetaData().getURL());
            }
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);

            if (result != null && result == 1) {
                log.info("쿼리 테스트(SELECT 1) : 성공!");
                log.info("프로젝트 설정과 DB 연결이 완벽합니다!");
            } else {
                log.error("쿼리 테스트 실패");
            }

        } catch (Exception e) {
            log.error("DB 연결 실패! application.yml을 확인하세요.", e);
        }
        
        log.info("==========================================");
    }
}