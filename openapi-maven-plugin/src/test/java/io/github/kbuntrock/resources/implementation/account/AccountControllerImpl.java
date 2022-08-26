package io.github.kbuntrock.resources.implementation.account;

import io.github.kbuntrock.resources.dto.*;
import io.github.kbuntrock.resources.endpoint.account.AccountController;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class AccountControllerImpl implements AccountController {

    @Override
    public void registerAccount(ManagedUserDto managedUserDto) {

    }

    @Override
    public void activateAccount(String key) {

    }

    @Override
    public AccountDto getAccount() {
        return null;
    }

    @Override
    public void saveAccount(AccountDto accountDto) {

    }

    @Override
    public void changePassword(PasswordChangeDto passwordChangeDto) {

    }

    @Override
    public List<PersistentTokenDto> getCurrentSessions() {
        return null;
    }

    @Override
    public void invalidateSession(String series) {

    }

    @Override
    public void requestPasswordReset(String mail) {

    }

    @Override
    public void finishPasswordReset(KeyAndPasswordDto keyAndPassword) {

    }

    @Override
    public String isAuthenticated(HttpServletRequest request) {
        return null;
    }
}
