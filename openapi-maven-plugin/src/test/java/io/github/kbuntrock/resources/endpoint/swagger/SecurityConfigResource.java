package io.github.kbuntrock.resources.endpoint.swagger;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(name = "configBearerAuth", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
@SecurityScheme(name = "configBasicAuth", type = SecuritySchemeType.HTTP, scheme = "basic")
public class SecurityConfigResource {
	// This is a configuration class, not a controller.
	// However, the security schemes defined here should be parsed and added to the OpenAPI specification.
}
