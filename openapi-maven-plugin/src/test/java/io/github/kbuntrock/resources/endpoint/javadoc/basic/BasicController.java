package io.github.kbuntrock.resources.endpoint.javadoc.basic;

import io.github.kbuntrock.resources.dto.Authority;
import io.github.kbuntrock.resources.dto.InterfaceDto;
import io.github.kbuntrock.resources.dto.TimeDto;
import io.github.kbuntrock.resources.dto.jackson.SimpleUserDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * This is my basic controller
 */
@RequestMapping("api")
public interface BasicController {

	/**
	 * Description of endpoint something3
	 * @param userId a user id as a long
	 * @param myDto a dto representing a SimpleUserDto
	 * @return true or false
	 */
	@PostMapping("something3")
	boolean getSomething(@RequestParam Long userId, @RequestBody SimpleUserDto myDto);

	/**
	 * Description of endpoint something1
	 * @param userId a user id as a string
	 * @param myDto  a dto still representing a SimpleUserDto
	 * @return still true or false
	 */
	@PostMapping("something1")
	boolean getSomething(@RequestParam String userId, @RequestBody SimpleUserDto myDto);

	/**
	 * Description of endpoint something2
	 * @param userId still a user id as a string
	 * @param myDto a dto representing an Authority
	 * @return 0 if false, 1 if true
	 */
	@PostMapping("something2")
	int getSomething(@RequestParam String userId, @RequestBody Authority myDto);

}
