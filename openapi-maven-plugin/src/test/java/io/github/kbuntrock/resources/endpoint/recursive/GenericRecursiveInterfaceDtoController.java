package io.github.kbuntrock.resources.endpoint.recursive;

import io.github.kbuntrock.resources.dto.AccountDto;
import io.github.kbuntrock.resources.dto.recursive.GenericRecursiveInterfaceDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Kevin Buntrock
 */
@RequestMapping("api")
public interface GenericRecursiveInterfaceDtoController {

    @GetMapping("recursive")
    GenericRecursiveInterfaceDto<AccountDto> getRecursive();

}
