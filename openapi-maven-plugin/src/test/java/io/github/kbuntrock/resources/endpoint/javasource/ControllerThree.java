package io.github.kbuntrock.resources.endpoint.javasource;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.AccountDto;
import io.github.kbuntrock.resources.dto.SpeAccountDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@RequestMapping(Constants.BASE_API + "/test-three")
public interface ControllerThree {

    @GetMapping("/account-page-one")
    boolean getAccountsPage(@RequestBody List<Map<Long, List<SpeAccountDto[]>[]>[]> test);

    @GetMapping("/account-page-two/{id}")
    List<Map<Long, List<SpeAccountDto[]>[]>[]> getAccountsPage(@PathVariable long id);

    @GetMapping("/account-page-three")
    List<Map<Long, List<SpeAccountDto[]>[]>[]> getAccountsPage(@RequestBody Map<Long, List<AccountDto[]>[]>[] body);

}
