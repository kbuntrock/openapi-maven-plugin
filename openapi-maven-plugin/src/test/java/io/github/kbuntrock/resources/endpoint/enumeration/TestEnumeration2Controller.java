package io.github.kbuntrock.resources.endpoint.enumeration;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.Authority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping(Constants.BASE_API + "/test-enum-2")
public interface TestEnumeration2Controller {

    @GetMapping()
    List<Authority> getAuthorities();
}
