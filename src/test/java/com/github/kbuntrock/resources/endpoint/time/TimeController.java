package com.github.kbuntrock.resources.endpoint.time;

import com.github.kbuntrock.resources.Constants;
import com.github.kbuntrock.resources.dto.TimeDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(Constants.BASE_API + "/time")
public interface TimeController {

    @GetMapping("/get-timedto")
    TimeDto getTimeDto();
}
