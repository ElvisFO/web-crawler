package br.com.crawler.exception;

/**
 * @author Elvis Fernandes on 24/02/19
 */
public class ExcelException extends RuntimeException {

    public ExcelException(String message) {
        super(message);
    }

    public ExcelException(String message, Throwable cause) {
        super(message, cause);
    }
}
