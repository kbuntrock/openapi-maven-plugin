package io.github.kbuntrock.resources.endpoint.generic;

import io.github.kbuntrock.resources.Constants;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Kevin Buntrock
 */
@RequestMapping(Constants.BASE_API + "/genericity-test-five")
public interface GenericityTestFive {


	@PostMapping(path = "update-something")
	void update(@RequestParam(value = "smthId", required = true) Long smthId,
		@RequestParam(value = "otherIds", required = false) List<Long> otherIds);
}
