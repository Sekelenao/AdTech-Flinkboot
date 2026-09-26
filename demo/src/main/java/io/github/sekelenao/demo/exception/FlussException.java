package io.github.sekelenao.demo.exception;

/**
 * Exception thrown when an error occurs while interacting with Apache Fluss.
 */
public class FlussException extends RuntimeException {

    public FlussException(String message, Throwable cause) {
        super(message, cause);
    }

}
