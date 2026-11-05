package io.github.kbuntrock.resources.endpoint.jspecify;

import io.github.kbuntrock.resources.dto.jspecify.NoScopeDto;
import io.github.kbuntrock.resources.dto.jspecify.NullMarkedDto;
import io.github.kbuntrock.resources.dto.jspecify.marked.PackageMarkedDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/jspecify")
public interface JSpecifyController {

	@GetMapping("/no-scope")
	NoScopeDto getNoScope();

	@GetMapping("/null-marked")
	NullMarkedDto getNullMarked();

	@GetMapping("/package-marked")
	PackageMarkedDto getPackageMarked();
}
