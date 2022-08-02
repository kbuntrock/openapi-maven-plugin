package io.github.kbuntrock.resources.endpoint.enumeration;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.Authority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(Constants.BASE_API + "/test-enum-2")
public interface TestEnumeration6Controller {

    @GetMapping("/{authority}")
    String getAuthorities(@PathVariable Authority authority);
}
