package io.github.kbuntrock.resources.endpoint.generic;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.criteria.SearchCriteriaChildV6;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Kevin Buntrock
 */
@RequestMapping(Constants.BASE_API + "/genericity-test-eleven")
public interface GenericityTestEleven {
	
	@PostMapping(path = "/search")
	public String search(@RequestBody SearchCriteriaChildV6 searchCriteria);
}
