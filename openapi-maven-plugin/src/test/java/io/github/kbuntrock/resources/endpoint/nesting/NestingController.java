package io.github.kbuntrock.resources.endpoint.nesting;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.AccountDto;
import io.github.kbuntrock.resources.dto.UserGroupDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(Constants.BASE_API + "/nesting")
public interface NestingController {

	@GetMapping("/{id}")
	AccountDto getById(@PathVariable(value = "id") Long id);

	@GetMapping("/usergroup/{id}")
	UserGroupDto getUsergroupById(@PathVariable(value = "id") Long id);

	@PostMapping("/usergroup")
	String setUsergroup(@RequestBody UserGroupDto usergroup);
}
