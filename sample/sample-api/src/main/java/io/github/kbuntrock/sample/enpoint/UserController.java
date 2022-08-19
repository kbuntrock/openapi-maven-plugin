package io.github.kbuntrock.sample.enpoint;

import io.github.kbuntrock.sample.Constants;
import io.github.kbuntrock.sample.dto.TestInterfaceDto;
import io.github.kbuntrock.sample.dto.UserGroupDto;
import io.github.kbuntrock.sample.dto.UserDto;
import io.github.kbuntrock.sample.dto.WrapperDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @GetMapping("/user-dtos-page")
    Page<UserDto> getUserDtoPage();

    @GetMapping("/interface-test-dto")
    TestInterfaceDto getInterfaceTestDto();

    @GetMapping("/optional-user")
    Optional<UserDto> getOptionalUser(@RequestParam boolean empty);

    @GetMapping("/wrapped-user")
    WrapperDto<UserDto> getWrappedUser();

    @GetMapping("/response-user")
    ResponseEntity<UserDto> getResponseUser();
}
