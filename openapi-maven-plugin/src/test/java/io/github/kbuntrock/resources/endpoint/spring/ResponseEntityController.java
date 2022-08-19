package io.github.kbuntrock.resources.endpoint.spring;

import io.github.kbuntrock.resources.dto.AccountDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * A controller designed to test response entities
 *
 * @author Kevin Buntrock
 */
@RequestMapping("api")
public interface ResponseEntityController {

    /**
     * Get an account object
     *
     * @return the return account
     */
    @GetMapping("account")
    ResponseEntity<AccountDto> getAccount();

}
