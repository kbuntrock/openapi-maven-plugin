package io.github.kbuntrock.resources.endpoint.generic;

import io.github.kbuntrock.resources.dto.genericity.extendsMap.ChildExtendMapUUID;
import io.github.kbuntrock.resources.dto.genericity.extendsMap.ExtendsMapLong;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Some map endpoint
 */
@RequestMapping("/map-extends")
public interface ExtendsMap {

	/**
	 * Get a child map
	 * @return a child map
	 */
	@GetMapping(path = "/child-map")
	ChildExtendMapUUID getChildMap();

	/**
	 * Post a "extend map"
	 * @param myMap some map
	 */
	@PostMapping(path = "/extend-map")
	void postExtendMap(@RequestBody ExtendsMapLong myMap);
}
