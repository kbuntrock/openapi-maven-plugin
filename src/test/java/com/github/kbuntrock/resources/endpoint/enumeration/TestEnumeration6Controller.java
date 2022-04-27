package com.github.kbuntrock.resources.endpoint.enumeration;

import com.github.kbuntrock.resources.Constants;
import com.github.kbuntrock.resources.dto.Authority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(Constants.BASE_API + "/test-enum-2")
public interface TestEnumeration6Controller {

    @GetMapping("/{authority}")
    String getAuthorities(@PathVariable Authority authority);
}
