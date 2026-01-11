package com.baedal.pass.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("K-Delivery Pass API Server")
                        .description("외국인을 위한 한국 배달 음식 주문 서비스 API 명세서")
                        .version("v1.0.0"));
    }
}