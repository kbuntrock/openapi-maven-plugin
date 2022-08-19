package io.github.kbuntrock.sample.rest;

import io.github.kbuntrock.sample.dto.*;
import io.github.kbuntrock.sample.enpoint.UserController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

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

    @Override
    public Page<UserDto> getUserDtoPage() {
        UserDto user1 = new UserDto();
        user1.setId(1L);
        user1.setFirstName("John");
        final List<UserDto> list = List.of(user1);
        return new Page<UserDto>() {
            @Override
            public int getTotalPages() {
                return 5;
            }

            @Override
            public long getTotalElements() {
                return 25;
            }

            @Override
            public <U> Page<U> map(Function<? super UserDto, ? extends U> converter) {
                return null;
            }

            @Override
            public int getNumber() {
                return 1;
            }

            @Override
            public int getSize() {
                return 4;
            }

            @Override
            public int getNumberOfElements() {
                return 6;
            }

            @Override
            public List<UserDto> getContent() {
                return list;
            }

            @Override
            public boolean hasContent() {
                return true;
            }

            @Override
            public Sort getSort() {
                return Sort.by("user_id");
            }

            @Override
            public boolean isFirst() {
                return true;
            }

            @Override
            public boolean isLast() {
                return false;
            }

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public Pageable nextPageable() {
                return null;
            }

            @Override
            public Pageable previousPageable() {
                return null;
            }

            @Override
            public Iterator<UserDto> iterator() {
                return list.iterator();
            }
        };
    }

    @Override
    public TestInterfaceDto getInterfaceTestDto() {
        return new TestInterfaceDto() {

            @Override
            public boolean isReturned1() {
                return true;
            }

            @Override
            public boolean hasReturned2() {
                return true;
            }

            @Override
            public boolean getReturned3() {
                return true;
            }

            @Override
            public boolean setReturned4() {
                return true;
            }

            @Override
            public boolean helloReturned5() {
                return true;
            }

            @Override
            public boolean nextReturned6() {
                return true;
            }

            @Override
            public boolean nextPage() {
                return false;
            }

            @Override
            public boolean getNextPage() {
                return false;
            }
        };
    }

    @Override
    public Optional<UserDto> getOptionalUser(boolean empty) {
        if(empty) {
            return Optional.empty();
        }
        UserDto user1 = new UserDto();
        user1.setId(1L);
        user1.setFirstName("John");
        return Optional.of(user1);
    }

    @Override
    public WrapperDto<UserDto> getWrappedUser() {
        UserDto user1 = new UserDto();
        user1.setId(1L);
        user1.setFirstName("John");
        UserDto user2 = new UserDto();
        user2.setId(2L);
        user2.setFirstName("John's child");
        UserDto user3 = new UserDto();
        user3.setId(3L);
        user3.setFirstName("John's child's child");
        WrapperDto wrapperDto = new WrapperDto<>(user1);
        wrapperDto.setChild(new WrapperDto(user2));
        wrapperDto.getChild().setChild(new WrapperDto(user3));
        return wrapperDto;
    }

    @Override
    public ResponseEntity<UserDto> getResponseUser() {
        UserDto user1 = new UserDto();
        user1.setId(1L);
        user1.setFirstName("John");
        return new ResponseEntity(user1, HttpStatus.valueOf(200));
    }
}
