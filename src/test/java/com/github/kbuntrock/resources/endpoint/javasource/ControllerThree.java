package com.github.kbuntrock.resources.endpoint.javasource;

import com.github.kbuntrock.resources.Constants;
import com.github.kbuntrock.resources.dto.AccountDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@RequestMapping(Constants.BASE_API + "/test-three")
public interface ControllerThree {

    @GetMapping("/account-page/{id}")
    List<Map<Long, List<AccountDto[]>[]>[]> getAccountsPage(@PathVariable long id);

}
