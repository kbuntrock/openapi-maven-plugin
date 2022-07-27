package com.github.kbuntrock.resources.endpoint.error;

import com.github.kbuntrock.resources.Constants;
import com.github.kbuntrock.resources.dto.Authority;
import com.github.kbuntrock.resources.dto.TimeDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author Kevin Buntrock
 */
@RequestMapping(Constants.BASE_API + "/same-operation")
public interface SameOperationController {

    @GetMapping()
    List<Authority> getAuthorities();

    @GetMapping()
    List<TimeDto> getTimes();
}
