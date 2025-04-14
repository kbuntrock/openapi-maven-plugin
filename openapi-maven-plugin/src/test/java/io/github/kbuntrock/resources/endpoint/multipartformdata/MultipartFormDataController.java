package io.github.kbuntrock.resources.endpoint.multipartformdata;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.multipartformdata.MetadataDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * Tests on multipart/form-data parameters
 */
@RequestMapping(Constants.BASE_API + "/file-controller")
public interface MultipartFormDataController {

	/**
	 * Upload a file into the cloud
	 * @param metadataDto Some metadata about this file
	 * @param goal The upload goal
	 * @param multipartFile The file being uploaded
	 */
	@PostMapping
	void uploadDocument(@RequestPart("metadata") MetadataDto metadataDto,
		@RequestPart String goal,
		@RequestPart("file") MultipartFile multipartFile);


	/**
	 * Upload two files into the cloud
	 * @param metadataDto Some metadata about this file
	 * @param goal The upload goal
	 * @param multipartFile The file 1 being uploaded
	 * @param multipartFile2 The file 2 being uploaded
	 */
	@PostMapping("/two")
	void uploadTwoDocuments(@RequestPart("metadata") MetadataDto metadataDto,
		@RequestPart String goal,
		@RequestPart("file1") MultipartFile multipartFile, @RequestPart("file2") MultipartFile multipartFile2);

	/**
	 * Upload multiple files into the cloud
	 * @param metadataDto Some metadata about this file
	 * @param goal The upload goal
	 * @param multipartFiles The files being uploaded
	 */
	@PostMapping("/multiple")
	void uploadMultipleDocuments(@RequestPart("metadata") MetadataDto metadataDto,
		@RequestPart(required = false) String goal,
		@RequestPart("files") MultipartFile[] multipartFiles);
}
