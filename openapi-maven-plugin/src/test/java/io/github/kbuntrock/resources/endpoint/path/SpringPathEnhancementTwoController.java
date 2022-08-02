package io.github.kbuntrock.resources.endpoint.path;

import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Kevin Buntrock
 */
public interface SpringPathEnhancementTwoController {

    @GetMapping("one")
    String getOne();

    @GetMapping
    String getTwo();
}
