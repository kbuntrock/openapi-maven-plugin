package io.github.kbuntrock.resources.endpoint.recursive;

import io.github.kbuntrock.resources.dto.AccountDto;
import io.github.kbuntrock.resources.dto.recursive.GenericRecursiveListDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Kevin Buntrock
 */
@RequestMapping("api")
public interface GenericRecursiveListDtoController {

    @GetMapping("recursive")
    GenericRecursiveListDto<AccountDto> getRecursive();

}
