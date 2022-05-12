package com.github.kbuntrock.resources.endpoint.nesting;

import com.github.kbuntrock.resources.Constants;
import com.github.kbuntrock.resources.dto.AccountDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(Constants.BASE_API + "/nesting")
public interface NestingController {

    @GetMapping("/{id}")
    AccountDto getById(@PathVariable(value = "id") Long id);
}
