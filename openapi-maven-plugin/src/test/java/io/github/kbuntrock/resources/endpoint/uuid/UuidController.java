package io.github.kbuntrock.resources.endpoint.uuid;

import io.github.kbuntrock.resources.Constants;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * UUID based controller
 */
@RequestMapping(Constants.BASE_API + "/uuid")
public interface UuidController {

	/**
	 * A beautiful service
	 *
	 * @param myUUID initial id
	 * @return a list of ids
	 */
	@GetMapping("/get-uuid-list")
	List<UUID> getUuidList(UUID myUUID);

}
