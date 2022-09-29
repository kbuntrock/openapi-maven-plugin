package io.github.kbuntrock.resources.endpoint.account;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.*;
import javax.ws.rs.*;
import java.util.List;

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
    List<AccountDto> getAccountDtosByQueryParam(@QueryParam("name") String name);

    @DELETE
    @Path("/delete/{id}")
    void deleteAccount(@PathParam("id") long id);
}
