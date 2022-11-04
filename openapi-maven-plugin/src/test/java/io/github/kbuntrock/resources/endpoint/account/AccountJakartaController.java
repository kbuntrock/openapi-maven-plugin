package io.github.kbuntrock.resources.endpoint.account;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.AccountDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import java.util.List;

/**
 * User account management
 */
@Path(Constants.BASE_API + "/account")
public interface AccountJakartaController {

	@PUT
	@Path("/update")
	AccountDto updateAccount(AccountDto userDto);

	@GET
	@Path("/user-dtos/{id}")
	List<AccountDto> getAccountDtos(@PathParam("id") long id);

	@POST
	@Path("/user-dtos")
	List<AccountDto> getAccountDtosByQueryParam(@QueryParam("name") @NotNull String name, HttpServletRequest request);

	@DELETE
	@Path("/delete/{id}")
	void deleteAccount(@PathParam("id") long id);
}
