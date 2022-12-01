package io.github.kbuntrock.resources.endpoint.collection;

import io.github.kbuntrock.resources.Constants;
import java.util.Collection;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Kévin Buntrock
 */
@RequestMapping(Constants.BASE_API + "/collection")
public interface CollectionController {

	@GetMapping()
	Collection<String> getStringCollection(@RequestBody Collection<Long> someIds);

}
