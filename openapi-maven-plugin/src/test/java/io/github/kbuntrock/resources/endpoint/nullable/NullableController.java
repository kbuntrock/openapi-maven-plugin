package io.github.kbuntrock.resources.endpoint.nullable;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.AccountDto;
import io.github.kbuntrock.resources.dto.UserGroupDto;
import io.github.kbuntrock.resources.dto.nullable.NullableDto;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/nullable")
public interface NullableController {

    @GetMapping("/{id}")
    NullableDto getById(@PathVariable(value = "id") Long id);
}
