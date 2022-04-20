package fr.github.kbuntrock.sample.dto;

import javax.validation.constraints.Size;

import static fr.github.kbuntrock.sample.Constants.PASSWORD_MAX_LENGTH;
import static fr.github.kbuntrock.sample.Constants.PASSWORD_MIN_LENGTH;


/**
 * View Model extending the AdminUserDTO, which is meant to be used in the user management UI for update
 *
 * This object should never be from the backend to the frontend
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

}
