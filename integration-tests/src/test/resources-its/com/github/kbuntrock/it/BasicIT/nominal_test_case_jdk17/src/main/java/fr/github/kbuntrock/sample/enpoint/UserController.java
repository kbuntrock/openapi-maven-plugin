package fr.github.kbuntrock.sample.enpoint;

import fr.github.kbuntrock.sample.Constants;
import fr.github.kbuntrock.sample.dto.UserDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(path = Constants.BASE_PATH + "/user")
public interface UserController {

    @PutMapping("/update")
    UserDto updateUser(@RequestBody UserDto userDto);

    @GetMapping("/user-dtos")
    List<UserDto> getUserDtos();
}
