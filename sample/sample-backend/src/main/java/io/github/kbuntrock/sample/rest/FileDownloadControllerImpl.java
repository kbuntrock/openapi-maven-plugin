package io.github.kbuntrock.sample.rest;

import io.github.kbuntrock.sample.enpoint.FileDownloadController;
import io.github.kbuntrock.sample.enpoint.FileUploadController;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kevin Buntrock
 */
@RestController
public class FileDownloadControllerImpl implements FileDownloadController {


    @Override
    public ResponseEntity<Resource> getResourceFile() {
        Resource resource = new FileSystemResource(getClass().getClassLoader().getResource("static/free_image.jpg").getPath());
        return new ResponseEntity<Resource>(resource, HttpStatus.OK);
    }
}
