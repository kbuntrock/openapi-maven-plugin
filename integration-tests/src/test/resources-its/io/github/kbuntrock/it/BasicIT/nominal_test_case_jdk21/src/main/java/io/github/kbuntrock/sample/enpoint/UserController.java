package io.github.kbuntrock.sample.enpoint;

import io.github.kbuntrock.sample.Constants;
import io.github.kbuntrock.sample.dto.UserDto;
import io.github.kbuntrock.sample.dto.RecordDto;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

/**
 * User related apis
 */
@RequestMapping(path = Constants.BASE_PATH + "/user")
public interface UserController {

    @PutMapping("/update")
    UserDto updateUser(@RequestBody UserDto userDto);

    @GetMapping("/user-dtos")
    List<UserDto> getUserDtos();
	
	/**
	 * List all the records
	 *
	 * @return all the available records
	 */
	@GetMapping("/records")
    List<RecordDto> getRecords();

    /**
     * Redirection somewhere
     * @return the return object should not be documented
     */
    @GetMapping("/issue262")
    RedirectView redirectUsingRedirectView();
}
