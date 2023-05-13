package io.github.kbuntrock.sample.implementation;

import io.github.kbuntrock.sample.dto.UserDto;
import io.github.kbuntrock.sample.enpoint.UserController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.AbstractController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
public class UserControllerImpl extends AbstractController implements UserController {

	@Override
	public UserDto updateUser(final UserDto userDto) {
		return null;
	}

	@Override
	public List<UserDto> getUserDtos() {
		return null;
	}

	@Override
	protected ModelAndView handleRequestInternal(final HttpServletRequest arg0, final HttpServletResponse arg1) throws Exception {
		return null;
	}
}
