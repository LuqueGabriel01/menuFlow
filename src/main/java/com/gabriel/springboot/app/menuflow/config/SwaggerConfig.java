package com.gabriel.springboot.app.menuflow.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.http.HttpHeaders;

@OpenAPIDefinition(
        info = @Info(
                title = "Restaurant Management",
                description = "This API is designed for the restaurant sector to help manage orders",
                termsOfService = "www.tablemanagement.com/security_policy",
                version = "1.0.0",
                contact = @Contact(
                        name = "Gabriel Luque",
                        url = "www.tablemanagement.com/contact",
                        email = "luquegabrieltm@gmail.com"
                ),
                license = @License(
                        name = "Standard software use for TableManagement",
                        url = "www.tablemanagement.com/licence"
                )
        ),
        servers = {
                 @Server(
                         description = "DEV SERVER",
                         url = "http://localhost:8080"
                 ),
                @Server(
                        description = "PROD SERVER",
                        url = "http://tablemanagement.com"
                )
        },
        security = @SecurityRequirement(
                name = "Bearer Authentication"
        )
)
@SecurityScheme(
        name = SwaggerConfig.SECURITY_SCHEME_NAME,
        description = "Access token for controllers",
        type = SecuritySchemeType.HTTP,
        paramName = HttpHeaders.AUTHORIZATION,
        in = SecuritySchemeIn.HEADER,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SwaggerConfig {
    public static final String SECURITY_SCHEME_NAME = "Bearer Authentication";
}
