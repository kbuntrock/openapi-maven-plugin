package io.github.kbuntrock.resources.endpoint.header;

import io.github.kbuntrock.resources.Constants;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * A controller about files and headers
 *
 * @author Kévin Buntrock
 */
@RequestMapping(Constants.BASE_API + "/file")
public interface MultipartFileWithHeaderController {

	/**
	 * Allow to upload a file
	 *
	 * @param headers        all the headers
	 * @param file           the provided file
	 * @param header1        the first header
	 * @param mySecondHeader the second header
	 * @return OK if upload in success, KO otherwise
	 */
	@PostMapping(value = "/upload")
	ResponseEntity<String> uploadFile(@RequestHeader HttpHeaders headers,
		@RequestParam("incomingFile") final MultipartFile file,
		@RequestHeader(value = "myFirstHeader", defaultValue = "toto") final String header1,
		@RequestHeader final String mySecondHeader);

}
