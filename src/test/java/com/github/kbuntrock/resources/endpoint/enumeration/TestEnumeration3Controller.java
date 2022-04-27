package com.github.kbuntrock.resources.endpoint.enumeration;

import com.github.kbuntrock.resources.Constants;
import com.github.kbuntrock.resources.dto.Authority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping(Constants.BASE_API + "/test-enum-3")
public interface TestEnumeration3Controller {

    @GetMapping()
    Authority getAuthority();
}
