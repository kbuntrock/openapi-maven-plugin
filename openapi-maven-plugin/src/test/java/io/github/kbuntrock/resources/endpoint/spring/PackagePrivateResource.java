package io.github.kbuntrock.resources.endpoint.spring;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Package private resource, with private and package private endpoint methods
 */
@RequestMapping("/package-private")
class PackagePrivateResource {

	@GetMapping("/all-package-private-endpoint")
	List<String> getAllPackagePrivate() {
		return null;
	}

	@GetMapping("/all-protected-endpoint")
	protected List<String> getAllProtected() {
		return null;
	}
}
