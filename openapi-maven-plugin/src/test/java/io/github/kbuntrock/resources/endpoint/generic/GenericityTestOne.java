package io.github.kbuntrock.resources.endpoint.generic;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.AccountDto;
import io.github.kbuntrock.resources.dto.GenericAggregationDto;
import io.github.kbuntrock.resources.dto.TimeDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Kevin Buntrock
 */
@RequestMapping(Constants.BASE_API + "/genericity-test-one")
public interface GenericityTestOne {

	@GetMapping("/first-object")
	GenericAggregationDto<TimeDto, AccountDto, String> getFirstObject();

	@PostMapping("/first-object")
	void setFirstObject(@RequestBody GenericAggregationDto<TimeDto, AccountDto, String> firstObject);

	@GetMapping("/second-object")
	GenericAggregationDto<Long, Boolean, AccountDto> getSecondObject();

	@PostMapping("/second-object")
	void setSecondObject(@RequestBody GenericAggregationDto<Long, Boolean, AccountDto> secondObject);
}
