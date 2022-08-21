package io.github.kbuntrock.sample.rest;

import io.github.kbuntrock.sample.dto.UserDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GenericDataController extends AbstractGenericData<UserDto> {

    @GetMapping("/datalist")
    @Override
    public List<UserDto> getDataList() {

        UserDto user1 = new UserDto();
        user1.setId(1L);
        user1.setFirstName("JohnL");
        UserDto user2 = new UserDto();
        user2.setId(2L);
        user2.setFirstName("First name number two");
        return List.of(user1, user2);
    }
}
