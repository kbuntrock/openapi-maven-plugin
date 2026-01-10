package io.github.kbuntrock.resources.endpoint.issues.issue263;

import io.github.kbuntrock.resources.dto.Authority;
import io.github.kbuntrock.resources.dto.PageDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("")
public interface ControllerTwo {

	/**
	 *
	 * @param evaluate
	 * @param authority
	 * @param file
	 *            a file in the "checkFile" case
	 * @return a PageDto of string in the "checkFile" case
	 */
	@PostMapping(path = "dummy", consumes = { "multipart/form-data" })
	PageDto<String> checkFile(@RequestParam(value = "evaluate", required = false) Boolean evaluate,
		@RequestParam(value = "authorityEnum", required = false) Authority authority,
		@RequestPart(value = "file", required = false) MultipartFile file);
}
