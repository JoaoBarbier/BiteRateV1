package com.progweb.biterate.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//Classe de configuração que o Spring usa pra gerar definições de Beans.
@Configuration
public class SwaggerConfig {

    //Seleciona um Bean pro IoC Container gerenciar e retora uma configuração exclusiva da OpenAPI.
    @Bean
    public OpenAPI openAPI() {
        // Define o nome do esquema de segurança para referência.
        final String schemeName = "bearerAuth";
        return new OpenAPI()
                // Configuração de informações nominais básicas da API.
                .info(new Info()
                        .title("Biterate API")
                        .description("API de avaliação de restaurantes")
                        .version("1.0"))
                // Exige autenticação em todos os endpoints (basicamente coloca o cadeado no Swagger)
                .addSecurityItem(new SecurityRequirement().addList(schemeName))

                // Configura o Swagger para usar Token JWT (Bearer).
                .components(new Components()
                        .addSecuritySchemes(schemeName, new SecurityScheme()
                                .name(schemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
