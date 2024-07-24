package io.github.kbuntrock.resources.endpoint.nullable;

import io.github.kbuntrock.resources.dto.nullable.NullableDto;
import io.github.kbuntrock.resources.dto.nullable.NullableGettersSettersDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/nullableGettersSetters")
public interface NullableGettersSettersController {

    @GetMapping("/{id}")
    NullableGettersSettersDto getById(@PathVariable(value = "id") Long id);
}
