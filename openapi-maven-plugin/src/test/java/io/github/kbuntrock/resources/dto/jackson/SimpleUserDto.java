package io.github.kbuntrock.resources.dto.jackson;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A simple user
 */
public class SimpleUserDto {

    /**
     * User firstname
     */
    private String username;

    /**
     * Whether the user is active or not
     */
    @JsonProperty("isActive")
    private boolean active;

    /**
     * Whether the user is an admin or not
     */
    private boolean admin;
}
