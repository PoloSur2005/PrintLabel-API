package com.printlabel.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI printLabelOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PrintLabel Manager API")
                        .description("""
                                API REST para generación de etiquetas de calzado.
                                
                                **Autenticación:**
                                1. Llama a `POST /api/v1/auth/login` con email y password.
                                2. Copia el valor del campo `token` de la respuesta.
                                3. Haz clic en el botón **Authorize 🔒** (arriba a la derecha).
                                4. En el campo `Value`, escribe: `Bearer <tu_token>` y presiona **Authorize**.
                                
                                Credenciales de prueba: `admin@printlabel.com` / `Admin2026!`
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("PrintLabel Team")
                                .email("dev@printlabel.com")))
                // Aplica BearerAuth globalmente a todos los endpoints (excepto los permitAll)
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Ingresa el JWT obtenido en /api/v1/auth/login. NO incluyas el prefijo 'Bearer ', Swagger lo agrega automáticamente.")
                        ));
    }
}