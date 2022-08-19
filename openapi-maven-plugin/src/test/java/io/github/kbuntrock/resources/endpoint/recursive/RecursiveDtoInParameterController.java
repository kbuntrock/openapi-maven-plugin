package io.github.kbuntrock.resources.endpoint.recursive;

import io.github.kbuntrock.resources.dto.recursive.RecursiveDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Kevin Buntrock
 */
@RequestMapping("api")
public interface RecursiveDtoInParameterController {

    @PostMapping("recursive")
    void postRecursive(@RequestBody RecursiveDto recursiveDto);

}
