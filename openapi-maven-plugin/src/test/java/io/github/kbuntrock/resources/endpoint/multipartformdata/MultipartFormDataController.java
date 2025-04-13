package io.github.kbuntrock.resources.endpoint.multipartformdata;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.multipartformdata.MetadataDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 */
@RequestMapping(Constants.BASE_API + "/file-controller")
public interface MultipartFormDataController {

	@PostMapping
	void uploadDocument(@RequestPart("metadata") MetadataDto metadataDto,
		@RequestPart String goal,
		@RequestPart("file") MultipartFile multipartFile);

}
