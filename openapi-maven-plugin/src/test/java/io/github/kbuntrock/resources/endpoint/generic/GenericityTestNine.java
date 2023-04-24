package io.github.kbuntrock.resources.endpoint.generic;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.criteria.SearchCriteriaV3;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Kevin Buntrock
 */
@RequestMapping(Constants.BASE_API + "/genericity-test-nine")
public interface GenericityTestNine {


	@PostMapping(path = "search")
	String findTerritoireGeographiqueByCriteria(@RequestBody SearchCriteriaV3 searchCriteria);
}
