package io.github.kbuntrock.resources.endpoint.issues.issue263;

import io.github.kbuntrock.resources.dto.TimeDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("dummy")
public interface ControllerOne {

    /**
     *
     * @param myTime a time in the "execute" case
     */
    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void execute(@RequestBody @Valid TimeDto myTime);
}
