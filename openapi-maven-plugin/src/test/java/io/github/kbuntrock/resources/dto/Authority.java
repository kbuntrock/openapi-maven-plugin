package io.github.kbuntrock.resources.dto;

/**
 * Permissions for a user
 */
public enum Authority {
    /**
     * Reading
     */
    READ_USER,
    /**
     * Writing
     */
    WRITE_USER,
    /**
     * Access to the application (the most basic permission)
     */
    ACCES_APP
}
