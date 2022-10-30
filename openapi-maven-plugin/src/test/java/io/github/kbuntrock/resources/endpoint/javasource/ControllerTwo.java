package io.github.kbuntrock.resources.endpoint.javasource;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.SpeAccountDto;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(Constants.BASE_API + "/test-two")
public interface ControllerTwo {

	@GetMapping("/account-page/{id}")
	List<Map<Long, List<SpeAccountDto[]>[]>[]> getAccountsPage(@PathVariable long id);

}
