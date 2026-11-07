package io.github.kbuntrock.resources.endpoint.jaxrs;

import io.github.kbuntrock.resources.Constants;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * void JAX-RS / Jakarta REST methods using the "customResponseTypeAnnotation" option (issue #439).
 */
@Path(Constants.BASE_API + "/void-response")
@jakarta.ws.rs.Path(Constants.BASE_API + "/void-response")
public class VoidResponseTypeJaxrsController {

	@GET
	@Path("/annotated")
	@Produces("application/octet-stream")
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/annotated")
	@jakarta.ws.rs.Produces("application/octet-stream")
	@ResponseType(byte[].class)
	public void annotated() {
	}

	@GET
	@Path("/not-annotated")
	@Produces("application/octet-stream")
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/not-annotated")
	@jakarta.ws.rs.Produces("application/octet-stream")
	public void notAnnotated() {
	}
}
