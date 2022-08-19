package io.github.kbuntrock.resources.endpoint.recursive;

import io.github.kbuntrock.resources.dto.AccountDto;
import io.github.kbuntrock.resources.dto.recursive.GenericRecursiveDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * A controller to test recursive generic return objects
 *
 * @author Kevin Buntrock
 */
@RequestMapping("api")
public interface GenericRecursiveDtoController {

    /**
     * Return the recursive generic object
     *
     * @return something
     */
    @GetMapping("recursive")
    GenericRecursiveDto<AccountDto> getRecursive();

}
