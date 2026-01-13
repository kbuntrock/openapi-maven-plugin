package io.github.kbuntrock.resources.endpoint.javadoc.markdown;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.kbuntrock.resources.dto.jackson.SimpleUserDto;

/// This controller is documented with **markdown** comments
///
/// No need for a html element to indicates a line break now
@RequestMapping("api")
public interface MarkdownCommentsController {

	/// Description of endpoint something3
	///
	/// Can do:
	/// - cool stuff number one
	/// - cool stuff number two
	///
	/// @param userId a user id as a long
	/// @param myDto a dto representing a SimpleUserDto
	/// @return true or false
	@PostMapping("something3")
	boolean getSomething(@RequestParam Long userId, @RequestBody SimpleUserDto myDto);

}
