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

/**
 * User controller interface
 */
@Path(Constants.BASE_PATH + "/user")
public interface UserController {

	String TEXT_BLOCK = """
		Example text
		with
		multiple
		lines""";


	/**
	 * Update a user
	 *
	 * @param userDto the user and his updated data
	 * @return the updated user
	 */
	@PUT
	@Path("/update")
	UserDto updateUser(UserDto userDto);

	/**
	 * Get a list of all the users of the application
	 *
	 * @return a list of all the users of the application
	 */
	@GET
	@Path("/user-dtos")
	List<UserDto> getUserDtos();

}
