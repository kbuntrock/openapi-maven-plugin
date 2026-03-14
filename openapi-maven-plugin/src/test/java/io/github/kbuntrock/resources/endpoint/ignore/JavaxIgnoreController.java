package io.github.kbuntrock.resources.endpoint.ignore;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.ignore.JavaxJsonTransientDto;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path(Constants.BASE_API + "/ignore")
public interface JavaxIgnoreController {

	@GET
	@Path("/get")
	JavaxJsonTransientDto get();
}
