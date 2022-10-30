package io.github.kbuntrock.resources.implementation.account;

import io.github.kbuntrock.resources.dto.AccountDto;
import io.github.kbuntrock.resources.dto.KeyAndPasswordDto;
import io.github.kbuntrock.resources.dto.ManagedUserDto;
import io.github.kbuntrock.resources.dto.PasswordChangeDto;
import io.github.kbuntrock.resources.dto.PersistentTokenDto;
import io.github.kbuntrock.resources.endpoint.account.AccountController;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountControllerImpl implements AccountController {

	@Override
	public void registerAccount(final ManagedUserDto managedUserDto) {

	}

	@Override
	public void activateAccount(final String key) {

	}

	@Override
	public AccountDto getAccount() {
		return null;
	}

	@Override
	public void saveAccount(final AccountDto accountDto) {

	}

	@Override
	public void changePassword(final PasswordChangeDto passwordChangeDto) {

	}

	@Override
	public List<PersistentTokenDto> getCurrentSessions() {
		return null;
	}

	@Override
	public void invalidateSession(final String series) {

	}

	@Override
	public void requestPasswordReset(final String mail) {

	}

	@Override
	public void finishPasswordReset(final KeyAndPasswordDto keyAndPassword) {

	}

	@Override
	public String isAuthenticated(final HttpServletRequest request) {
		return null;
	}
}
