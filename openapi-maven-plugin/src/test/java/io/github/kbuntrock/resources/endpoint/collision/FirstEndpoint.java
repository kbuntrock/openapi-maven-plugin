package io.github.kbuntrock.resources.endpoint.collision;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.AccountDto;
import java.util.Collection;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Kévin Buntrock
 */
@RequestMapping(Constants.BASE_API + "/first-ws")
public interface FirstEndpoint {

	@GetMapping()
	Collection<AccountDto> getCompleteAccounts();

	@PostMapping("/authority")
	Collection<AccountDto> findByAuthority(@RequestBody Authority authority);

	public class Authority {

		private String name;

		private int level;
	}
}
