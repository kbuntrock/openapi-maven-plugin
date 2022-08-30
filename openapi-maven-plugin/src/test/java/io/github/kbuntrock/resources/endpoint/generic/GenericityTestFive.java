package io.github.kbuntrock.resources.endpoint.generic;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.TimeDto;
import io.github.kbuntrock.resources.dto.TypedDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Kevin Buntrock
 */
@RequestMapping(Constants.BASE_API + "/genericity-test-five")
public interface GenericityTestFive {


    @PostMapping(path = "update-something")
    void update(@RequestParam(value = "smthId", required = true) Long smthId, @RequestParam(value = "otherIds", required = false) List<Long> otherIds);
}
