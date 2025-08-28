package io.github.kbuntrock.resources.endpoint.innerclass;

import io.github.kbuntrock.resources.Constants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * A controller testing documentation on inner and local classes
 */
@RequestMapping(Constants.BASE_API + "/inner-class-object-controller")
public interface InnerAndLocalClassObjectsController {

	/**
	 * Get a user by its id
	 * @param userId the user id
	 * @return a user inner class object
	 */
	@GetMapping("/user")
	default UserInnerDto get(@RequestParam int userId) {

		/**
		 * This is a local class
		 */
		class UselessLocalClass {
			/**
			 * a pretty useless boolean
			 */
			boolean local;

			public boolean isLocal() {
				return local;
			}
		}

		UselessLocalClass localClass = new UselessLocalClass();
		localClass.local = true;
		if(localClass.isLocal()) {
			return new UserInnerDto();
		}

		return null;
	}

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
