package fr.github.kbuntrock.sample.enpoint;

import fr.github.kbuntrock.sample.Constants;
import fr.github.kbuntrock.sample.dto.PageDto;
import fr.github.kbuntrock.sample.dto.TimeDto;
import fr.github.kbuntrock.sample.dto.UserDto;
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
