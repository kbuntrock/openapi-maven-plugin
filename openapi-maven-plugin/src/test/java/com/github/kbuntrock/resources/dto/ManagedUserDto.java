package com.github.kbuntrock.resources.dto;

import javax.validation.constraints.Size;

import static com.github.kbuntrock.resources.Constants.PASSWORD_MAX_LENGTH;
import static com.github.kbuntrock.resources.Constants.PASSWORD_MIN_LENGTH;

/**
 * View Model extending the AdminUserDTO, which is meant to be used in the user management UI.
 */
public class ManagedUserDto extends AdminUserDto {

    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;

    public ManagedUserDto() {
        // Empty constructor needed for Jackson.
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ManagedUserVM{" + super.toString() + "} ";
    }
}
