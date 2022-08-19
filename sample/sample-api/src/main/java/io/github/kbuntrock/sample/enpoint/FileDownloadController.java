package io.github.kbuntrock.sample.enpoint;

import io.github.kbuntrock.sample.Constants;
import org.apache.coyote.Response;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Kevin Buntrock
 */
@RequestMapping(path = Constants.BASE_PATH + "/file-download")
public interface FileDownloadController {

    @GetMapping(path = "resource-file", produces = "application/octet-stream")
    ResponseEntity<Resource> getResourceFile();
}
