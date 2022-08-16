package io.github.kbuntrock.sample.enpoint;

import io.github.kbuntrock.sample.Constants;
import io.github.kbuntrock.sample.dto.UserDto;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import java.util.List;

@Path(Constants.BASE_PATH + "/user")
public interface UserController {

    @PUT
    @Path("/update")
    UserDto updateUser(UserDto userDto);

    @GET
    @Path("/user-dtos/{id}")
    List<UserDto> getUserDtos(@PathParam("id") long id);

    @POST
    @Path("/user-dtos/{name}")
    List<UserDto> getUserDtosByQueryParam(@QueryParam("name") String name);
}
