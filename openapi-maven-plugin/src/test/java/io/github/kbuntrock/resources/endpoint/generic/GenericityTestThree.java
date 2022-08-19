package io.github.kbuntrock.resources.endpoint.generic;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.AccountDto;
import io.github.kbuntrock.resources.dto.WrapperDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Kevin Buntrock
 */
@RequestMapping(Constants.BASE_API + "/genericity-test-three")
public interface GenericityTestThree {

    @GetMapping("/get-account/{id}")
    WrapperDto<AccountDto> getPageAccount(@PathVariable int id);
}
