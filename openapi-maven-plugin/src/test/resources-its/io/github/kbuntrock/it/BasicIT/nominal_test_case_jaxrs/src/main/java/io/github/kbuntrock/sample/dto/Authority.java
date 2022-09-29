package io.github.kbuntrock.sample.dto;

/**
 * Permissions of a user
 */
public enum Authority {
    /**
     * Has the right to access the application
     */
    ACCESS_APP,
    /**
     * Can read user informations
     */
    READ_USER,
    /**
     * Can update user informations
     */
    UPDATE_USER
}
