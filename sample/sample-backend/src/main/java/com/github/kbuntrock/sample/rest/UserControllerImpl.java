package com.github.kbuntrock.sample.rest;

import fr.github.kbuntrock.sample.dto.Authority;
import fr.github.kbuntrock.sample.dto.UserDto;
import fr.github.kbuntrock.sample.dto.UserGroupDto;
import fr.github.kbuntrock.sample.enpoint.UserController;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
public class UserControllerImpl implements UserController {

    @Override
    public List<String> getAllUsernames() {
        return List.of("tata", "yoyo");
    }

    @Override
    public int getNbUsers() {
        return 24;
    }

    @Override
    public List<Integer> getNumberList() {
        return List.of(4, 899, 7, 100, 0, 5);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        userDto.setAuthorities(Set.of(Authority.ACCESS_APP, Authority.READ_USER));
        userDto.setCreatedDate(Instant.now());
        userDto.setCreatedBy("Kévin");
        userDto.setId(1000L);
        userDto.setImageUrl("http://monimage.klee.fr");

        return userDto;
    }

    @Override
    public UserGroupDto getUsergroupById(Long id) {
        UserGroupDto userGroupDto = new UserGroupDto();

        UserDto leader = new UserDto();
        leader.setId(1L);
        leader.setFirstName("JohnL");
        userGroupDto.setLeader(leader);

        UserDto leader2 = new UserDto();
        leader2.setId(2L);
        leader2.setFirstName("KevL");
        userGroupDto.setLeader(leader);

        UserGroupDto userGroupDto1 = new UserGroupDto();
        userGroupDto1.setLeader(leader2);
        userGroupDto1.setMembers(List.of(leader, leader2));

        userGroupDto.setMainSubgroup(userGroupDto1);

        return userGroupDto;
    }

    @Override
    public String setUsergroup(UserGroupDto usergroup) {
        return usergroup.getLeader().getEmail() + " with " + usergroup.getSubgroups().size() + " group in the list (" +
                usergroup.getSubgroups().get(0).getMembers().size() + " members)";
    }

    @Override
    public List<UserDto> getUserDtos() {

        UserDto user1 = new UserDto();
        user1.setId(1L);
        user1.setFirstName("John");

        UserDto user2 = new UserDto();
        user2.setId(2L);
        user2.setFirstName("Kev");

        return List.of(user1, user2);
    }
}
