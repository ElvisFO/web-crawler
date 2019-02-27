package br.com.crawler;

import br.com.crawler.component.Crawler;
import br.com.crawler.component.License;

/**
 * @author Elvis Fernandes on 24/02/19
 */
public class Application {

    public static void main(String[] args){

        Crawler crawler = new Crawler();

        try {
            License.allowExecution();
            crawler.processor();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.exit(1);
    }
}
