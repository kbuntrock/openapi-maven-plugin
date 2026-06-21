package io.github.kbuntrock.resources.endpoint.swagger;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
@SecurityScheme(name = "basicAuth", type = SecuritySchemeType.HTTP, scheme = "basic")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api")
@Path("/api")
public class SecurityAnnotationResource {

	@GetMapping("/summary")
	@GET
	@Path("/summary")
	public String getSummary() {
		return "Summary";
	}

	@SecurityRequirement(name = "basicAuth")
	@GetMapping("/summary_basic")
	@GET
	@Path("/summary_basic")
	public String getSummaryBasicAuth() {
		return "Summary";
	}
}
