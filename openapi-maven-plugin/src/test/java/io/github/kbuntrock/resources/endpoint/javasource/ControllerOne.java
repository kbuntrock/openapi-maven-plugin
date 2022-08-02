package io.github.kbuntrock.resources.endpoint.javasource;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.SpeAccountDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@RequestMapping(Constants.BASE_API + "/test-one")
public interface ControllerOne {

    @GetMapping("/account-page")
    boolean getAccountsPage(@RequestBody List<Map<Long, List<SpeAccountDto[]>[]>[]> test);

}
