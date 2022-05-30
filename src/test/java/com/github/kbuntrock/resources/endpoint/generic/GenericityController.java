package com.github.kbuntrock.resources.endpoint.generic;

import com.github.kbuntrock.resources.Constants;
import com.github.kbuntrock.resources.dto.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(Constants.BASE_API + "/generic")
public interface GenericityController {

    @GetMapping("/account-page")
    PageDto<AccountDto> getAccountsPage();

    @GetMapping("/time-page")
    PageDto<TimeDto> getTimePage();

    @GetMapping("/authority-page")
    PageDto<Authority> getAuthorityPage();

    @PostMapping("/account-page")
    String setAccountsPage(@RequestBody PageDto<AccountDto> accounts);
}
