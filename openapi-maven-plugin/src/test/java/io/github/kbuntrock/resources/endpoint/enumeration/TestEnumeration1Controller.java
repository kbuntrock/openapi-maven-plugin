package io.github.kbuntrock.resources.endpoint.enumeration;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.EnumTest1Dto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(Constants.BASE_API + "/test-enum-1")
public interface TestEnumeration1Controller {

    @GetMapping()
    @Deprecated
    EnumTest1Dto getAuthorities();
}
