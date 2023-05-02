package io.github.kbuntrock.resources.endpoint.jaxrs;

import io.github.kbuntrock.resources.dto.AccountDto;
import javax.ws.rs.core.Response;

/**
 * @author Kévin Buntrock
 */
public abstract class AbstractJaxrsController {


	@ResponseType(AccountDto.class)
	public abstract Response indirectlyPresent();

}
