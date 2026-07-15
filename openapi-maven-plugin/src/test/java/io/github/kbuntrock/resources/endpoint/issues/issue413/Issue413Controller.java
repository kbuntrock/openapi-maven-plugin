package io.github.kbuntrock.resources.endpoint.issues.issue413;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
@SecuritySchemes({ @SecurityScheme(type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.COOKIE) })
public class Issue413Controller {

	@GetMapping("/{name}")
	public String hello(@PathVariable String name) {
		return "Hello " + name;
	}
}
