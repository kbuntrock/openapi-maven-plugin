package io.github.kbuntrock.sample.enpoint;

import io.github.kbuntrock.sample.Constants;
import io.github.kbuntrock.sample.dto.UserDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User controller interface
 */
@RequestMapping(path = Constants.BASE_PATH + "/user")
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
	@PutMapping("/update")
	UserDto updateUser(@RequestBody UserDto userDto);

	/**
	 * Get a list of all the users of the application
	 *
	 * @return a list of all the users of the application
	 */
	@GetMapping("/user-dtos")
	List<UserDto> getUserDtos();
}
