package br.com.crawler.component;

import br.com.crawler.exception.LicenseException;

import java.time.LocalDate;

/**
 * @author Elvis Fernandes on 24/02/19
 */
public class License {

    private static final LocalDate expiration = LocalDate.of(2019, 02, 27);

    public static void allowExecution() {

        if (LocalDate.now().isAfter(expiration)) {

            throw new LicenseException("A licença expirou, entre em contato com o desenvolvedor para adquirir o software.");
        }
    }
}
