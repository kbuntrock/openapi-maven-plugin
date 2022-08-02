package io.github.kbuntrock.sample.enpoint;

import io.github.kbuntrock.sample.Constants;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Kevin Buntrock
 */
@RequestMapping(path = Constants.BASE_PATH + "/file-upload")
public interface FileUploadController {

    @PostMapping(path = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    List<String> uploadFiles(@RequestParam long myId, @RequestParam(name = "files") MultipartFile[] files);

    @PostMapping(path = "single-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadSingleFile(@RequestParam long myId, @RequestParam(name = "file") MultipartFile file);

    @PostMapping(path = "non-required-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    List<String> uploadNonRequiredFiles(@RequestParam long myId, @RequestParam(name = "files", required = false) MultipartFile[] files);
}
