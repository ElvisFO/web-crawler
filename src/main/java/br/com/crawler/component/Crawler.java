package br.com.crawler.component;

import br.com.crawler.exception.ExcelException;
import br.com.crawler.model.Dados;
import br.com.crawler.util.FileUtils;
import com.gargoylesoftware.htmlunit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.*;

import javax.security.sasl.AuthenticationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Elvis Fernandes on 24/02/19
 */
public class Crawler {

    private Excel excel;

    public Crawler() {
        this.excel = new Excel();
    }

    public void processor() throws Exception {


        Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        List<Dados> dadosExcel;

        HtmlUnitDriver driver = new HtmlUnitDriver(BrowserVersion.CHROME, true);
        driver.manage().timeouts().setScriptTimeout(50, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(50, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS);

        driver.get("https://webvendas.gvt.com.br/siebelview/dealer/login.sv");

        this.login(driver);

        driver.findElement(By.id("formTemplate:coverageAddress")).click();

        dadosExcel = this.excel.getExcelData();

        if (dadosExcel == null || dadosExcel.isEmpty()) {
            throw new ExcelException("Não existe dados na planilha para ser processados.");
        }

        for (Dados dados : dadosExcel) {

            try {

                if ((dados.getNumeroImovel() != null && dados.getNumeroImovel() > 0) && dados.getCep() != null) {

                    System.out.println("Processando cep: " + dados.getCep());

                    driver.executeScript(String.format("document.getElementsByName('formTemplate:j_id29')[0].value = '%s'", dados.getCep()));
                    driver.executeScript("document.getElementsByName('formTemplate:j_id29')[0].onchange();");


                    this.waitForJStoLoad(driver, 5000);

                    if (driver.findElement(By.name("formTemplate:j_id29")).getAttribute("value").equals("")) {
                        System.out.println("Cep não está no formato aceito pelo site: " + dados.getCep());
                        this.refresh(driver);
                        continue;
                    }

                    driver.executeScript(String.format("document.getElementById('formTemplate:unitNumber').value = '%s'", dados.getNumeroImovel()));

                    driver.findElement(By.id("formTemplate:findAddressbtnPesquisar")).click();

                    this.waitForJStoLoad(driver, 10000);

                    if (this.registerNotFound(driver)) continue;

                    driver.findElement(By.id("formTemplate:dtLogradouros:n:0")).click();

                    this.waitForJStoLoad(driver, 4000);

                    driver.executeScript("document.getElementById('formTemplate:j_id108').onclick();");

                    this.waitForJStoLoad(driver, 5000);

                    System.out.println(driver.getCurrentUrl());

                    if (driver.getCurrentUrl().contains("returnCoverage")) {

                        dados.setTecnologia(driver.findElement(By.xpath("//*[@id=\"formTemplate:mainPanel_body\"]/fieldset/table[1]/tbody/tr[2]/td[3]")).getText());
                        dados.setLinhas(driver.findElement(By.xpath("//*[@id=\"formTemplate:mainPanel_body\"]/fieldset/table[1]/tbody/tr[2]/td[4]")).getText());
                        dados.setVinteMb(driver.findElement(By.xpath("//*[@id=\"formTemplate:mainPanel_body\"]/fieldset/table[1]/tbody/tr[2]/td[5]")).getText());
                        dados.setCinquentaMb(driver.findElement(By.xpath("//*[@id=\"formTemplate:mainPanel_body\"]/fieldset/table[1]/tbody/tr[2]/td[6]")).getText());
                        dados.setTrezentosMb(driver.findElement(By.xpath("//*[@id=\"formTemplate:mainPanel_body\"]/fieldset/table[1]/tbody/tr[2]/td[7]")).getText());

                        driver.findElement(By.id("formTemplate:j_id114")).click();
                        this.waitForJStoLoad(driver, 4000);
                        System.out.println("Cep processado com sucesso: " + dados.getCep());

                    } else {

                        this.refresh(driver);
                    }
                }
            } catch (Exception ex) {
                System.out.println("Exception: " +ex.getMessage());
                System.err.println("Cep com erro: " + dados.getCep());
                this.refresh(driver);
            }
        }

        this.excel.generateExcel(dadosExcel);
    }

    private void login(HtmlUnitDriver driver) throws IOException{

        File file = FileUtils.findFileByName("login.txt");

        List<String> lines = Files.readAllLines(file.toPath());

        if(lines == null || lines.isEmpty()) {
            throw new IOException("É necessário criar o arquivo login.txt com usuário e senha.");
        }

        String[] login = lines.get(0).split(";");
        String user = login[0].trim();
        String password = login[1].trim();

        driver.findElement(By.id("login")).sendKeys(user);
        driver.executeScript(String.format("document.getElementById('senha').value = '%s'", password));
        driver.findElement(By.name("j_id194")).click();

        if(driver.getCurrentUrl().contains("login")) {
            throw new AuthenticationException("Usuário ou senha inválidos");
        }
    }

    private void refresh(HtmlUnitDriver driver) throws InterruptedException {

        driver.navigate().refresh();
        this.waitForJStoLoad(driver, 3000);
    }

    private boolean registerNotFound(HtmlUnitDriver driver) {

        try {
            driver.findElement(By.xpath("//*[@id=\"formTemplate:dtLogradouros:noDataRow\"]/td"));
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean waitForJStoLoad(HtmlUnitDriver driver, long sleepInMilliSeconds) throws InterruptedException {

        WebDriverWait wait = new WebDriverWait(driver, 50);

        // wait for jQuery to load
        ExpectedCondition<Boolean> jQueryLoad = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    return ((Long)js.executeScript("return jQuery.active") == 0);
                }
                catch (Exception e) {
                    return true;
                }
            }
        };

        // wait for Javascript to load
        ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                return js.executeScript("return document.readyState")
                        .toString().equals("complete");
            }
        };

        Thread.sleep(sleepInMilliSeconds);

        return wait.until(jQueryLoad) && wait.until(jsLoad);
    }

}
