package io.github.kbuntrock.resources.endpoint.generic;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.AccountDto;
import io.github.kbuntrock.resources.dto.TerritoryEnum;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Kevin Buntrock
 */
@RequestMapping(Constants.BASE_API + "/genericity-test-six")
public interface GenericityTestSix {


	@PostMapping(path = "authority-map")
	Map<TerritoryEnum, List<? extends AccountDto>> findTerritoireGeographiqueByCriteria(@RequestParam Long sectionId);
}
