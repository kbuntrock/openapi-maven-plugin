package com.github.kbuntrock.resources.endpoint.file;

import com.github.kbuntrock.resources.Constants;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping(Constants.BASE_API + "/file-upload")
public interface FileUploadController {

    @PostMapping(path = "upload-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    List<String> uploadFiles(@RequestParam MultipartFile[] files);
}
