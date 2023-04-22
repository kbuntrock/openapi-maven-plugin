package io.github.kbuntrock.resources.endpoint.generic;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.criteria.SearchCriteria;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Kevin Buntrock
 */
@RequestMapping(Constants.BASE_API + "/genericity-test-seven")
public interface GenericityTestSeven {


	@PostMapping(path = "search")
	String findTerritoireGeographiqueByCriteria(@RequestBody SearchCriteria searchCriteria);
}
