package com.github.kbuntrock.resources.dto;


import com.github.kbuntrock.resources.Constants;

import javax.validation.constraints.*;
import java.util.Set;

/**
 * Object utilisé uniquement pour gérer l'identification d'un utilisateur côté client
 */
public class AccountDto {

    /**
     * Dummy static strings
     */
    private static final Long RESERVED_SYSTEM_USER_ID = 0L;
    public static final Long NO_USER_ID = -1L;

    @NotNull
    @NotBlank
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    private String login;

    @NotNull
    @Size(max = 50)
    private String firstName;

    @NotNull
    @Size(max = 50)
    private String lastName;

    @NotNull
    @Email
    @Size(min = 5, max = 254)
    private String email;

    @Size(max = 256)
    private String imageUrl;

    @NotNull
    private boolean activated = false;

    @NotNull
    @Size(min = 2, max = 10)
    private String langKey;

    @NotNull
    private Set<Authority> authorities;

    public AccountDto() {
        // Empty constructor needed for Jackson.
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }
}
