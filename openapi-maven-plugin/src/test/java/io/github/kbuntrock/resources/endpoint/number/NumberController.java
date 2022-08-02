package io.github.kbuntrock.resources.endpoint.number;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.NumberDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * @author Kevin Buntrock
 */
@RequestMapping(Constants.BASE_API + "/file-upload")
public interface NumberController {

    @GetMapping("the-number")
    NumberDto getTheNumberDto(@RequestParam BigDecimal bigDecimal);
}
