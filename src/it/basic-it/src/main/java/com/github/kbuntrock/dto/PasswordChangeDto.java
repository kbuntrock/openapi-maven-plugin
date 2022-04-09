package com.github.kbuntrock.dto;

/**
 * A DTO representing a password change required data - current and new password.
 */
public class PasswordChangeDto {

    private String currentPassword;

    private String newPassword;

    public PasswordChangeDto() {
        // Empty constructor needed for Jackson.
    }

    public PasswordChangeDto(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
