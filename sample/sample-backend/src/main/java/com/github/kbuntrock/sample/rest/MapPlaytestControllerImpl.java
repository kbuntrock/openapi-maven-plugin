package com.github.kbuntrock.sample.rest;

import com.github.kbuntrock.sample.dto.UserDto;
import com.github.kbuntrock.sample.enpoint.MapPlaytestController;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class MapPlaytestControllerImpl implements MapPlaytestController {

    private AtomicLong idGenerator = new AtomicLong(1L);

    @Override
    public Map<Long, UserDto> getMapUsers() {
        Map<Long, UserDto> map = new LinkedHashMap<>();
        UserDto user1 = createUserDto("John");
        UserDto user2 = createUserDto("Bob");
        UserDto user3 = createUserDto("Kev");
        map.put(user1.getId(), user1);
        map.put(user2.getId(), user2);
        map.put(user3.getId(), user3);
        return map;
    }

    @Override
    public Map<String, String> getMapString() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("first", "toto");
        map.put("second", "tata");
        return map;
    }

    @Override
    public String postMapInt(Map<Integer, String> map) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            builder.append(entry.getKey() + ":" + entry.getValue() + "; ");
        }
        return builder.toString();
    }

    @Override
    public String postMapAccount(Map<String, UserDto> map) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, UserDto> entry : map.entrySet()) {
            builder.append(entry.getKey() + ":" + entry.getValue().getEmail() + ";");
        }
        return builder.toString();
    }

    private UserDto createUserDto(String firstname) {
        UserDto user = new UserDto();
        user.setId(idGenerator.getAndAdd(1));
        user.setFirstName(firstname);
        return user;
    }
}
