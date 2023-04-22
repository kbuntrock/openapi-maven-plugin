package io.github.kbuntrock.resources.endpoint.generic;

import java.util.List;
import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Kévin Buntrock
 */
public abstract class EntityDtoResource<DTO, sts> extends StatusResources<sts> {

	private static final String POST_ENTITY_DTOS = "/push";
	private static final String POST_ONLY_ONE = "/push-one";

	@POST
	@Path(POST_ENTITY_DTOS)
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Path(POST_ENTITY_DTOS)
	@PostMapping(POST_ENTITY_DTOS)
	public Response postEntityDtos(@RequestBody @Valid final List<DTO> dtos) {
		return null;
	}

	@POST
	@Path(POST_ONLY_ONE)
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Path(POST_ONLY_ONE)
	@PostMapping(POST_ONLY_ONE)
	public Response postOnlyOne(@RequestBody @Valid final DTO dto) {
		return null;
	}

}
