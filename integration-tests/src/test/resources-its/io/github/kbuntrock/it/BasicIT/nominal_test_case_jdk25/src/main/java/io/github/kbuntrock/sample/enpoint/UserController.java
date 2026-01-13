package io.github.kbuntrock.sample.enpoint;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import io.github.kbuntrock.sample.Constants;
import io.github.kbuntrock.sample.dto.RecordDto;
import io.github.kbuntrock.sample.dto.UserDto;

/// User related **apis**
///
/// Can do:
/// - update a user
/// - get all users
/// - get all records
/// - redirect to something
///
/// Enjoy! 😉
@RequestMapping(path = Constants.BASE_PATH + "/user")
public interface UserController {

	/// Update the **user**
	/// @param userDto The user to update
	@PutMapping("/update")
	UserDto updateUser(@RequestBody UserDto userDto);

	/// Get all **users**
	/// @return a list of users
	@GetMapping("/user-dtos")
	List<UserDto> getUserDtos();

	/**
	 * List all the records. This comment is still in the old javadoc formalism.
	 *
	 * @return all the available records
	 */
	@GetMapping("/records")
	List<RecordDto> getRecords();

	/// Redirect somewhere
	/// @return the return object should not be documented
	@GetMapping("/issue262")
	RedirectView redirectUsingRedirectView();
}
