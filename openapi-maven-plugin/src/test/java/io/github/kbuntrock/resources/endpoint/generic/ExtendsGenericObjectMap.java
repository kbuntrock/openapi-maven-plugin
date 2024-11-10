package io.github.kbuntrock.resources.endpoint.generic;

import io.github.kbuntrock.resources.dto.genericity.extendsMap.GenericExtendsObjectMap;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/extends-generic-object-map")
public class ExtendsGenericObjectMap {

	@GetMapping(path = "/map")
	public GenericExtendsObjectMap<UUID> getMap() {
		GenericExtendsObjectMap<UUID> map = new GenericExtendsObjectMap();
		List<UUID> uuidList = new ArrayList<>();
		map.rows(uuidList);
		List<Long> longList = new ArrayList<>();
		map.lines(longList);
		return map;
	}

}
