package io.github.kbuntrock.resources.endpoint.recursive;

import io.github.kbuntrock.resources.dto.AccountDto;
import io.github.kbuntrock.resources.dto.recursive.GenericInterfaceRecursiveListDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Kevin Buntrock
 */
@RequestMapping("api")
public interface GenericRecursiveInterfaceListDtoInParameterController {

    @PostMapping("recursive")
    void postRecursive(@RequestBody GenericInterfaceRecursiveListDto<AccountDto> body);

}
