package io.github.kbuntrock.sample.implementation;

import io.github.kbuntrock.sample.dto.UserDto;
import io.github.kbuntrock.sample.enpoint.UserController;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserControllerImpl implements UserController {

    @Override
    public UserDto updateUser(UserDto userDto) {
        return null;
    }

    @Override
    public List<UserDto> getUserDtos() {
        return null;
    }
}
