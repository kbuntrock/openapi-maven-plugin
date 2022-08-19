package io.github.kbuntrock.resources.endpoint.spring;

import io.github.kbuntrock.resources.dto.AccountDto;
import io.github.kbuntrock.resources.dto.WrapperDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

/**
 * @author Kevin Buntrock
 */
@RequestMapping("api")
public interface OptionalController {

    @GetMapping("account")
    Optional<AccountDto> getAccount();

    @GetMapping("wrapped-account")
    Optional<WrapperDto<AccountDto>> getWrappedAccount();

}
