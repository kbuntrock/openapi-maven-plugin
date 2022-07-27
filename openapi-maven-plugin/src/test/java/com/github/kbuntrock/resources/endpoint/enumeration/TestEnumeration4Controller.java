package com.github.kbuntrock.resources.endpoint.enumeration;

import com.github.kbuntrock.resources.Constants;
import com.github.kbuntrock.resources.dto.EnumTest1Dto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(Constants.BASE_API + "/test-enum-1")
public interface TestEnumeration4Controller {

    @PostMapping()
    String getAuthorities(@RequestBody EnumTest1Dto enumTest1Dto);
}
