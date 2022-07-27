package com.github.kbuntrock.resources.endpoint.path;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Kevin Buntrock
 */
@RequestMapping("api")
public interface SpringPathEnhancementOneController {

    @GetMapping("one")
    String getOne();

    @GetMapping
    String getTwo();
}
