package fr.github.kbuntrock.sample.enpoint;

import fr.github.kbuntrock.sample.Constants;
import fr.github.kbuntrock.sample.dto.UserDto;
import fr.github.kbuntrock.sample.dto.UserGroupDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping(path = Constants.BASE_PATH + "/user")
public interface UserController {

    @GetMapping("/users")
    List<String> getAllUsernames();

    @GetMapping("/nb-users")
    int getNbUsers();

    @GetMapping("/number-list")
    List<Integer> getNumberList();

    @PutMapping("/update")
    UserDto updateUser(@RequestBody UserDto userDto);

    @GetMapping("/usergroup/{id}")
    UserGroupDto getUsergroupById(@PathVariable(value = "id") Long id);

    @PostMapping("/usergroup")
    String setUsergroup(@RequestBody UserGroupDto usergroup);

    @GetMapping("/user-dtos")
    List<UserDto> getUserDtos();
}
