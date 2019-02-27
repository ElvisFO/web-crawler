package br.com.crawler.exception;

/**
 * @author Elvis Fernandes on 24/02/19
 */
public class LicenseException extends RuntimeException {

    public LicenseException(String message) {
        super(message);
    }

    public LicenseException(String message, Throwable cause) {
        super(message, cause);
    }
}
