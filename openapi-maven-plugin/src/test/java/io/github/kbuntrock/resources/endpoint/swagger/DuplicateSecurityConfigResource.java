package io.github.kbuntrock.resources.endpoint.swagger;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
public class DuplicateSecurityConfigResource {
	// This defines a "bearerAuth" security scheme, which conflicts with SecurityAnnotationResource.
}
