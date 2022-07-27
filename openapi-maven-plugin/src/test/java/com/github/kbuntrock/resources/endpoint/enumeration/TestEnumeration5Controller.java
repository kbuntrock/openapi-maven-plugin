package com.github.kbuntrock.resources.endpoint.enumeration;

import com.github.kbuntrock.resources.Constants;
import com.github.kbuntrock.resources.dto.Authority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping(Constants.BASE_API + "/test-enum-2")
public interface TestEnumeration5Controller {

    @PostMapping()
    String getAuthorities(@RequestBody List<Authority> authorityList);
}
