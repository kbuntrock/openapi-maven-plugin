package io.github.kbuntrock.resources.endpoint.generic;


import io.github.kbuntrock.resources.dto.genericity.ActionDto;
import io.github.kbuntrock.resources.dto.genericity.StatusImplDto;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Kévin Buntrock
 */
@Path("/actions")
@jakarta.ws.rs.Path("/actions")
@RequestMapping("/actions")
public class ActionResource extends EntityDtoResource<ActionDto, StatusImplDto> {

	private static final String GET_ALL_PATH = "/all/{since}";

	@GET
	@Path(GET_ALL_PATH)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path(GET_ALL_PATH)
	@GetMapping(GET_ALL_PATH)
	public List<ActionDto> getAll(@PathParam("since") @jakarta.ws.rs.PathParam("since") @PathVariable("since") final long since) {
		return null;
	}
}
