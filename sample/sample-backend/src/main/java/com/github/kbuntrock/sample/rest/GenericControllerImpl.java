package com.github.kbuntrock.sample.rest;

import fr.github.kbuntrock.sample.dto.PageDto;
import fr.github.kbuntrock.sample.dto.TimeDto;
import fr.github.kbuntrock.sample.dto.UserDto;
import fr.github.kbuntrock.sample.enpoint.GenericController;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Kevin Buntrock
 */
@RestController
public class GenericControllerImpl implements GenericController {

    @Override
    public PageDto<UserDto> setAccountPage(PageDto<UserDto> accountPage) {
        return accountPage;
    }

    @Override
    public PageDto<TimeDto> setTimePage(PageDto<TimeDto> timePage) {
        return timePage;
    }
}
