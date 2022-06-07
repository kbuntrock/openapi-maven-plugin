package com.github.kbuntrock.resources.endpoint.generic;

import com.github.kbuntrock.resources.Constants;
import com.github.kbuntrock.resources.dto.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping(Constants.BASE_API + "/generic")
public interface GenericityTestTwo {

    @GetMapping("/account-page")
    ArrayDto<AccountDto>[] getAccountsPage(@RequestParam long id);

    @GetMapping("/time-page")
    ArrayDto<TimeDto> getTimePage();

    @GetMapping("/authority-page")
    PageDto<Authority> getAuthorityPage();

    @PostMapping("/account-page")
    String setAccountsPage(@RequestBody PageDto<AccountDto> accounts);

    @GetMapping(path = "/account-map/{perimeterId}", produces = MediaType.APPLICATION_JSON_VALUE)
    Map<String, AccountDto[]> getAccountMap(@PathVariable Long perimeterId);

    @GetMapping(path = "/authority-page-map/{perimeterId}", produces = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Map<Long, List<List<AccountDto>[]>>>[] getAuthorityPageMap(@PathVariable Long perimeterId);
}
