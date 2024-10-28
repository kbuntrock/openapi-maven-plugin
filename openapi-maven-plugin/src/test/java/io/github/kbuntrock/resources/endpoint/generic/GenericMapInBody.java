package io.github.kbuntrock.resources.endpoint.generic;


import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Generic map in body
 * @param <T> a generic type
 */
public interface GenericMapInBody<T> {

	/**
	 * Post map in body
	 * @param myMap my map
	 */
	@PostMapping(path = "/object-map")
	void postObject(@RequestBody Map<String, T> myMap);

}
