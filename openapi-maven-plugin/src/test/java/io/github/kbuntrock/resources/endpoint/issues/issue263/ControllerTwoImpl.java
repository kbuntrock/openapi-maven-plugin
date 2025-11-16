package io.github.kbuntrock.resources.endpoint.issues.issue263;

import io.github.kbuntrock.resources.dto.Authority;
import io.github.kbuntrock.resources.dto.PageDto;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ControllerTwoImpl implements ControllerTwo{
    @Override
    public PageDto<String> checkFile(Boolean evaluate, Authority authority, MultipartFile file) {
        return null;
    }
}
