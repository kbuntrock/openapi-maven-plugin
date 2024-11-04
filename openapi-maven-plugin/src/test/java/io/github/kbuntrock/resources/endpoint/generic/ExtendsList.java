package io.github.kbuntrock.resources.endpoint.generic;

import io.github.kbuntrock.resources.dto.genericity.extendsList.ExtendsListUUID;
import io.github.kbuntrock.resources.dto.genericity.extendsMap.ChildExtendMapUUID;
import io.github.kbuntrock.resources.dto.genericity.extendsMap.ExtendsMapLong;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Some list endpoint
 */
@RequestMapping("/list-extends")
public interface ExtendsList {

	/**
	 * Get a list of uuids
	 * @return uuid list
	 */
	@GetMapping(path = "/list")
	ExtendsListUUID getListUUID();
}
