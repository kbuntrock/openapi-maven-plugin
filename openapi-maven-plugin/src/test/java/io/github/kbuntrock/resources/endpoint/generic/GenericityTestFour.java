package io.github.kbuntrock.resources.endpoint.generic;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.TimeDto;
import io.github.kbuntrock.resources.dto.TypedDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Kevin Buntrock
 */
@RequestMapping(Constants.BASE_API + "/genericity-test-four")
public interface GenericityTestFour {

    @GetMapping("/get-typed-dto/{id}")
    TypedDto<TimeDto> getTypedDto(@PathVariable int id);
}
