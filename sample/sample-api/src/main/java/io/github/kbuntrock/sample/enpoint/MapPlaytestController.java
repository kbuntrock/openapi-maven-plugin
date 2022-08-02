package io.github.kbuntrock.sample.enpoint;

import io.github.kbuntrock.sample.Constants;
import io.github.kbuntrock.sample.dto.UserDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequestMapping(path = Constants.BASE_PATH + "/map-playtest")
public interface MapPlaytestController {

    @GetMapping("/get-map-users")
    Map<Long, UserDto> getMapUsers();

    @GetMapping("/get-map-string")
    Map<String, String> getMapString();

    @PostMapping("/post-map-int")
    String postMapInt(@RequestBody Map<Integer, String> map);

    @PostMapping("/post-map-account")
    String postMapAccount(@RequestBody Map<String, UserDto> map);
}
