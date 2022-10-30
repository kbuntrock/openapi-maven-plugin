package io.github.kbuntrock.resources.endpoint.error;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.Authority;
import io.github.kbuntrock.resources.dto.TimeDto;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Kevin Buntrock
 */
@RequestMapping(Constants.BASE_API + "/same-operation")
public interface SameOperationController {

	@GetMapping()
	List<Authority> getAuthorities();

	@GetMapping()
	List<TimeDto> getTimes();
}
