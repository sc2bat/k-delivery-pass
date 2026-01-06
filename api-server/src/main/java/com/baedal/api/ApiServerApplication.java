package com.baedal.api;

import org.springframework.boot.SpringApplication;
import lombok.extern.slf4j.Slf4j; 
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@Slf4j
@SpringBootApplication
public class ApiServerApplication {

	public static void main(String[] args) {
		try {
			Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
			dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
			log.info(" Environment variables loaded successfully. DB URL: {}", System.getProperty("DB_URL"));
            
        } catch (Exception e) {
            log.error(" .env file not found. (Safe to ignore in production environment)", e);
        }
		SpringApplication.run(ApiServerApplication.class, args);
	}

}
