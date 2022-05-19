package com.github.kbuntrock.resources.endpoint.generic;

import com.github.kbuntrock.resources.Constants;
import com.github.kbuntrock.resources.dto.AccountDto;
import com.github.kbuntrock.resources.dto.PageDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(Constants.BASE_API + "/generic")
public interface GenericityController {

    @GetMapping("/account-page")
    PageDto<AccountDto> getAccountsPage();
}
