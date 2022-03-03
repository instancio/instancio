package org.instancio.exception;

public class InstancioException extends RuntimeException {

    public InstancioException(String message) {
        super(message);
    }

    public InstancioException(String message, Throwable cause) {
        super(message, cause);
    }
}
