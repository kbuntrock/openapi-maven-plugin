package io.github.kbuntrock.resources.endpoint.optional.object;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.optional.object.OptionalClassDto;
import io.github.kbuntrock.resources.dto.optional.object.OptionalInterfaceDto;
import java.util.Optional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Kévin Buntrock
 */
@Path(Constants.BASE_API + "/optional-controller")
@jakarta.ws.rs.Path(Constants.BASE_API + "/optional-controller")
@RequestMapping(Constants.BASE_API + "/optional-controller")
public interface OptionalController {

	@GetMapping("class-value")
	@GET
	@Path("/class-value")
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/class-value")
	OptionalClassDto getClassValue();

	@GetMapping("interface-value")
	@GET
	@Path("/interface-value")
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/interface-value")
	OptionalInterfaceDto getInterfaceValue();

	@GetMapping("optional-parameter")
	@GET
	@Path("/optional-parameter")
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/optional-parameter")
	String optionalParameter(@RequestParam("myParam") @QueryParam("myParam") @jakarta.ws.rs.QueryParam("myParam") Optional<String> myParam);

	@GetMapping("default-parameter")
	@GET
	@Path("/default-parameter")
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/default-parameter")
	String optionalParameter(
		@RequestParam(value = "myParam", defaultValue = "default-value") @QueryParam("myParam") @jakarta.ws.rs.QueryParam("myParam") String myParamWithDefault);

}