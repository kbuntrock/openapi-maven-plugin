package io.github.kbuntrock.resources.endpoint.enumeration;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.TerritoryEnum;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(Constants.BASE_API + "/test-enum-7")
public interface TestEnumeration7Controller {

	@GetMapping("/territories")
	TerritoryEnum getTerritories();
}
