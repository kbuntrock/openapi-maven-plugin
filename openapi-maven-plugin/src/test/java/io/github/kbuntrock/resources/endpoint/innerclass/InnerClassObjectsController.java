package io.github.kbuntrock.resources.endpoint.innerclass;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.NumberDto;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(Constants.BASE_API + "/inner-class-object-controller")
public interface InnerClassObjectsController {

	/**
	 * Get an user by its id
	 * @param userId the user id
	 * @return a user inner class object
	 */
	@GetMapping("/user")
	UserInnerDto get(@RequestParam int userId);

	/**
	 * An inner class object representing a user
	 */
	class UserInnerDto {

		/**
		 * The last name of the user
		 */
		private String lastname;
		/**
		 * The first name of the user
		 */
		private String firstname;
	}
}
