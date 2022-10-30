package io.github.kbuntrock.resources.endpoint.javadoc.inheritance.two;

import io.github.kbuntrock.resources.dto.PageDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A beautiful description of ChildClassTwo
 *
 * @author Kevin Buntrock
 */
@RestController
public class ChildClassTwo implements IChildClassTwo {


	/**
	 * Supported functionalities, as a page
	 *
	 * @return the supported functionalities
	 */
	@GetMapping("/functionalities")
	public PageDto<String> getPageFunctionalities() {
		return null;
	}


	@Override
	public boolean canPrettyPrint() {
		return false;
	}

	@Override
	public String giveMeMyAgePlusOne(final int age) {
		return null;
	}

	@Override
	public boolean canEncapsulate() {
		return false;
	}
}
