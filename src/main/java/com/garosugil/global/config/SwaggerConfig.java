package com.garosugil.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Shade Road API")
                        .description("가로수길 프로젝트 API 문서")
                        .version("v1.0.0"))
                .servers(List.of(
                        new Server().url("/api").description("Base API Server")
                ));
    }
}
