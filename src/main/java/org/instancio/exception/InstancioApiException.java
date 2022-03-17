package org.instancio.exception;

public class InstancioApiException extends InstancioException {

    public InstancioApiException(String message) {
        super(message);
    }

    public InstancioApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
