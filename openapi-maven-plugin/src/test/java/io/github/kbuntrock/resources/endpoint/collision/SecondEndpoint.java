package io.github.kbuntrock.resources.endpoint.collision;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.collision.AccountDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Kévin Buntrock
 */
@RequestMapping(Constants.BASE_API + "/second-ws")
public interface SecondEndpoint {

	@GetMapping("/light")
	AccountDto[] getPartialAccounts();
}
