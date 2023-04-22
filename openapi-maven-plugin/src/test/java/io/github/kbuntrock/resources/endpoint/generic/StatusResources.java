package io.github.kbuntrock.resources.endpoint.generic;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Kévin Buntrock
 */
public abstract class StatusResources<StatusDto> {

	@GET
	@Path("/status")
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/status")
	@GetMapping("/status")
	public StatusDto getStatus() {
		return null;
	}

}
