package io.github.kbuntrock.resources.endpoint.jackson.inputoutput;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.jackson.OrderJacksonDto;
import io.github.kbuntrock.resources.dto.jackson.ResumeJacksonDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A controller named JacksonController4
 */
@RequestMapping(Constants.BASE_API + "/4")
@RestController
public interface JacksonController4 {

	/**
	 * Create a resume
	 *
	 * @param input
	 *            the input object to create a resume
	 */
	@PostMapping("/resume")
	void createResume(@RequestBody Optional<Set<List<Optional<Map<String, ResumeJacksonDto>>>>> input);

	/**
	 * Create something
	 *
	 * @param input
	 *            the input object
	 */
	@PostMapping()
	void create(@RequestBody Optional<Set<List<Optional<Map<String, OrderJacksonDto>>>>> input);

}
