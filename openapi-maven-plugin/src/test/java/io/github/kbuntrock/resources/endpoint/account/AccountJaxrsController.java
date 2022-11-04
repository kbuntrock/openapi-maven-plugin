package io.github.kbuntrock.resources.endpoint.account;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.AccountDto;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

/**
 * User account management
 */
@Path(Constants.BASE_API + "/account")
public interface AccountJaxrsController {

	@PUT
	@Path("/update")
	AccountDto updateAccount(AccountDto userDto);

	@GET
	@Path("/user-dtos/{id}")
	List<AccountDto> getAccountDtos(@PathParam("id") long id);

	@POST
	@Path("/user-dtos")
	List<AccountDto> getAccountDtosByQueryParam(@NotNull @QueryParam("name") String name, HttpServletRequest request);

	@DELETE
	@Path("/delete/{id}")
	void deleteAccount(@PathParam("id") long id);
}
