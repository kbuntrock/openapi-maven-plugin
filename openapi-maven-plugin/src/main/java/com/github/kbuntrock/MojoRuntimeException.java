package com.github.kbuntrock;

/**
 * @author Kevin Buntrock
 */
public class MojoRuntimeException extends RuntimeException {

    public MojoRuntimeException(String message) {
        super(message);
    }

    public MojoRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
