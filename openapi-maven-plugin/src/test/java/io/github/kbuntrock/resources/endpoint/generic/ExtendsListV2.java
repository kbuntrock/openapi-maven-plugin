package io.github.kbuntrock.resources.endpoint.generic;

import io.github.kbuntrock.resources.dto.genericity.extendsMap.GenericExtendsList;
import io.github.kbuntrock.resources.dto.genericity.extendsMap.GenericExtendsObjectMap;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/extends-list-v2")
public interface ExtendsListV2 {


	@GetMapping(path = "/")
	GenericExtendsList<Long> getList();

}
