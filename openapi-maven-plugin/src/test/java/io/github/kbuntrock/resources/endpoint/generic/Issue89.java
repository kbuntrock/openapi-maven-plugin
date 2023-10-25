package io.github.kbuntrock.resources.endpoint.generic;

import io.github.kbuntrock.resources.dto.genericity.issue89.Bar;
import io.github.kbuntrock.resources.dto.genericity.issue89.IBar;
import java.util.Collection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Webservices about the issue number 89
 *
 * @author Kevin Buntrock
 */
@RequestMapping(Issue89.API_PREFIX)
public interface Issue89 {

	String API_PREFIX = "/issue89";

	/**
	 * This api test the issue n°89
	 *
	 * @param myParam my awesome parameter
	 * @return my also awesome response
	 */
	@PostMapping(path = "/youAreAwesome-applications")
	ResponseEntity<Collection<IBar>> youAreAwesome(@RequestBody Bar myParam);
}
