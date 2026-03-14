package io.github.kbuntrock.resources.endpoint.ignore;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.ignore.JakartaJsonTransientDto;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path(Constants.BASE_API + "/ignore")
public interface JakartaIgnoreController {

	@GET
	@Path("/get")
	JakartaJsonTransientDto get();
}
