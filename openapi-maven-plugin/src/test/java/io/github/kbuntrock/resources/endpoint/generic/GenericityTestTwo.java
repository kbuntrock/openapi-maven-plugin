package io.github.kbuntrock.resources.endpoint.generic;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.AccountDto;
import io.github.kbuntrock.resources.dto.ArrayDto;
import io.github.kbuntrock.resources.dto.Authority;
import io.github.kbuntrock.resources.dto.PageDto;
import io.github.kbuntrock.resources.dto.TimeDto;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(Constants.BASE_API + "/generic")
public interface GenericityTestTwo {

	@GetMapping("/account-page")
	ArrayDto<AccountDto>[] getAccountsPage(@RequestParam long id);

	@GetMapping("/time-page")
	ArrayDto<TimeDto> getTimePage();

	@GetMapping("/authority-page")
	PageDto<Authority> getAuthorityPage();

	@PostMapping("/account-page")
	String setAccountsPage(@RequestBody PageDto<AccountDto> accounts);

	@GetMapping(path = "/account-map/{perimeterId}", produces = MediaType.APPLICATION_JSON_VALUE)
	Map<String, AccountDto[]> getAccountMap(@PathVariable Long perimeterId);

	@GetMapping(path = "/authority-page-map/{perimeterId}", produces = MediaType.APPLICATION_JSON_VALUE)
	Map<String, Map<Long, List<List<AccountDto>[]>>>[] getAuthorityPageMap(@PathVariable Long perimeterId);
}
