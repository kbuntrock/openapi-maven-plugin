package io.github.kbuntrock.resources.endpoint.file;

import io.github.kbuntrock.resources.Constants;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Kevin Buntrock
 */
@RequestMapping(Constants.BASE_API + "/stream")
public interface StreamResponseController {

    @GetMapping(value = "/stream", produces = "application/octet-stream")
    ResponseEntity<Resource> getStream();
}
