package io.github.kbuntrock.resources.endpoint.number;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.NumberDto;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Kevin Buntrock
 */
@RequestMapping(Constants.BASE_API + "/file-upload")
public interface NumberController {

	@GetMapping("the-number")
	NumberDto getTheNumberDto(@RequestParam BigDecimal bigDecimal);
}
