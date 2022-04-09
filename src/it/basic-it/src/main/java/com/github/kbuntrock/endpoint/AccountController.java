package com.github.kbuntrock.endpoint;

import com.github.kbuntrock.Constants;
import com.github.kbuntrock.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping(Constants.BASE_API + "/account")
public interface AccountController {

    /**
     * {@code POST  /account/register} : register the user.
     *
     * @param managedUserDto the managed user View Model.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    void registerAccount(@RequestBody ManagedUserDto managedUserDto);

    /**
     * {@code GET  /account/activate} : activate the registered user.
     *
     * @param key the activation key.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be activated.
     */
    @GetMapping("/activate")
    void activateAccount(@RequestParam(value = "key") String key);

    /**
     * {@code GET  /account} : get the current user.
     *
     * @return the current user.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @GetMapping
    AccountDto getAccount();

    /**
     * {@code POST  /account} : update the current user information.
     *
     * @param accountDto the current user information.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user login wasn't found.
     */
    @PostMapping
    void saveAccount(@RequestBody AccountDto accountDto);

    /**
     * {@code POST  /account/change-password} : changes the current user's password.
     *
     * @param passwordChangeDto current and new password.
     */
    @PostMapping(path = "/change-password")
    void changePassword(@RequestBody PasswordChangeDto passwordChangeDto);

    /**
     * {@code GET  /account/sessions} : get the current open sessions.
     *
     * @return the current open sessions.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the current open sessions couldn't be retrieved.
     */
    @GetMapping("/sessions")
    List<PersistentTokenDto> getCurrentSessions();

    /**
     * {@code DELETE  /account/sessions?series={series}} : invalidate an existing session.
     * <p>
     * - You can only delete your own sessions, not any other user's session
     * - If you delete one of your existing sessions, and that you are currently logged in on that session, you will
     * still be able to use that session, until you quit your browser: it does not work in real time (there is
     * no API for that), it only removes the "remember me" cookie
     * - This is also true if you invalidate your current session: you will still be able to use it until you close
     * your browser or that the session times out. But automatic login (the "remember me" cookie) will not work
     * anymore.
     * There is an API to invalidate the current session, but there is no API to check which session uses which
     * cookie.
     *
     * @param series the series of an existing session.
     * @throws IllegalArgumentException if the series couldn't be URL decoded.
     */
    @DeleteMapping("/sessions/{series}")
    void invalidateSession(@PathVariable(name = "series") String series);

    /**
     * {@code POST   /account/reset-password/init} : Send an email to reset the password of the user.
     *
     * @param mail the mail of the user.
     */
    @PostMapping(path = "/reset-password/init")
    void requestPasswordReset(@RequestBody String mail);

    /**
     * {@code POST   /account/reset-password/finish} : Finish to reset the password of the user.
     *
     * @param keyAndPassword the generated key and the new password.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the password could not be reset.
     */
    @PostMapping(path = "/reset-password/finish")
    void finishPasswordReset(@RequestBody KeyAndPasswordDto keyAndPassword);

    /**
     * {@code GET  /authenticate} : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request.
     * @return the login if the user is authenticated.
     */
    @GetMapping("/authenticate")
    String isAuthenticated(HttpServletRequest request);
}
