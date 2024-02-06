package io.github.kbuntrock.resources.endpoint.jackson;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.jackson.SimpleUserDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Jackson Json property controller
 */
@RequestMapping(Constants.BASE_API + "/users")
@RestController
public interface JacksonJsonPropertyController {

    /**
     * {@code GET  /users} : get the list of users
     *
     * @return the list of users.
     */
    @RequestMapping(method = RequestMethod.GET)
    List<SimpleUserDto> findAll();
}
