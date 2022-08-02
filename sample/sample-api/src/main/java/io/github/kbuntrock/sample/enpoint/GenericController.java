package io.github.kbuntrock.sample.enpoint;

import io.github.kbuntrock.sample.Constants;
import io.github.kbuntrock.sample.dto.TimeDto;
import io.github.kbuntrock.sample.dto.PageDto;
import io.github.kbuntrock.sample.dto.UserDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Kevin Buntrock
 */
@RequestMapping(path = Constants.BASE_PATH + "/generics")
public interface GenericController {

    @PostMapping("/user-page")
    PageDto<UserDto> setAccountPage(@RequestBody PageDto<UserDto> accountPage);

    @PostMapping("/time-page")
    PageDto<TimeDto> setTimePage(@RequestBody PageDto<TimeDto> timePage);
}
