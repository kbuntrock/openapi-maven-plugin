package io.github.kbuntrock.resources.endpoint.generic;

import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Generic map of objects in body
 */
@RequestMapping("/generic-mapping")
public interface GenericMappingObject extends GenericMapInBody<Object> {

}
