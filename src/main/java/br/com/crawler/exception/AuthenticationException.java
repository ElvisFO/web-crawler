package br.com.crawler.exception;

/**
 * @author Elvis Fernandes on 24/02/19
 */
public class AuthenticationException extends RuntimeException {


    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
