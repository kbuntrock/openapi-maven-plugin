package io.github.kbuntrock.resources.endpoint.jaxrs;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.AccountDto;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * User account management
 */
@Path(Constants.BASE_API + "/response")
@jakarta.ws.rs.Path(Constants.BASE_API + "/response")
public class ResponseJaxrsController extends AbstractJaxrsController {

	@Override
	@GET
	@Path("/update-indirectly")
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/update-indirectly")
	public Response indirectlyPresent() {
		return null;
	}

	@GET
	@Path("/update-directly")
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/update-directly")
	@ResponseType(AccountDto.class)
	public Response directlyPresent() {
		return null;
	}

}
