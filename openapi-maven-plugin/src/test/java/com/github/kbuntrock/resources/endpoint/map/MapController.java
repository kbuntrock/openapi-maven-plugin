package com.github.kbuntrock.resources.endpoint.map;

import com.github.kbuntrock.resources.Constants;
import com.github.kbuntrock.resources.dto.AccountDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequestMapping(Constants.BASE_API + "/map")
public interface MapController {

    @GetMapping("/get-map-users")
    Map<Long, AccountDto> getMapUsers();

    @GetMapping("/get-map-string")
    Map<String, String> getMapString();

    @PostMapping("/post-map-int")
    String postMapInt(@RequestBody Map<Integer, String> map);

    @PostMapping("/post-map-account")
    String postMapAccount(@RequestBody Map<Integer, AccountDto> map);
}
