package br.com.crawler.component;

import br.com.crawler.exception.ExcelException;
import br.com.crawler.model.Dados;
import br.com.crawler.util.FileUtils;
import br.com.crawler.util.Utils;
import lombok.Cleanup;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.poi.ss.usermodel.Row.MissingCellPolicy.CREATE_NULL_AS_BLANK;

/**
 * @author Elvis Fernandes on 24/02/19
 */
public class Excel {

    private static final int CEP = 6;
    private static final int TECNOLOGIA = 7;
    private static final int LINHAS = 8;
    private static final int VINTEMB = 9;
    private static final int CINQUENTAMB = 10;
    private static final int TREZENTOSMB = 11;

    public List<Dados> getExcelData() {

        List<Dados> dadosList = new ArrayList<>();


        try {

            File file = FileUtils.findFileByName("Base_consulta.xlsx");
            @Cleanup FileInputStream excelFile = new FileInputStream(file);
            @Cleanup XSSFWorkbook workbook = new XSSFWorkbook(excelFile);

            XSSFSheet sheet = workbook.getSheetAt(0);

            List<Row> rows = (List<Row>) Utils.toList(sheet.iterator());

            rows.remove(0);

            rows = rows.stream()
                    .filter(row -> !Utils.toList(row.cellIterator()).isEmpty())
                    .collect(Collectors.toList());

            rows.forEach(row -> {

                List<Cell> cells = (List<Cell>) Utils.toList(row.cellIterator());

                dadosList.add(this.mountData(cells));

            });

        } catch(IOException ex) {

            throw new ExcelException("Erro ao ler planilha", ex);
        }

        return dadosList;
    }


    public void generateExcel(List<Dados> dadosList) {

        try {

            File file = FileUtils.findFileByName("Base_consulta.xlsx");
            @Cleanup FileInputStream excelFile = new FileInputStream(file);
            @Cleanup XSSFWorkbook workbook = new XSSFWorkbook(excelFile);

            XSSFSheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rows = sheet.iterator();

            List<Row> rowsList = (List<Row>) Utils.toList(sheet.iterator());

            rowsList = rowsList.stream()
                    .filter(row -> !Utils.toList(row.cellIterator()).isEmpty())
                    .collect(Collectors.toList());

            rows = (Iterator<Row>) Utils.toIterator(rowsList);

            while (rows.hasNext()) {

                Row row = rows.next();

                if (row.getRowNum() == 0) continue;

                double cellCep = row.getCell(CEP, CREATE_NULL_AS_BLANK).getNumericCellValue();
                long cepFormat = Utils.convertToLong(cellCep);

                Optional<Dados> optional = dadosList.stream()
                        .filter(dado -> dado.getCep().equals(cepFormat))
                        .findFirst();

                if (optional.isPresent()) {

                    row.getCell(TECNOLOGIA, CREATE_NULL_AS_BLANK).setCellValue(optional.get().getTecnologia());
                    row.getCell(LINHAS, CREATE_NULL_AS_BLANK).setCellValue(optional.get().getLinhas());
                    row.getCell(VINTEMB, CREATE_NULL_AS_BLANK).setCellValue(optional.get().getVinteMb());
                    row.getCell(CINQUENTAMB, CREATE_NULL_AS_BLANK).setCellValue(optional.get().getCinquentaMb());
                    row.getCell(TREZENTOSMB, CREATE_NULL_AS_BLANK).setCellValue(optional.get().getTrezentosMb());
                }


            }

            this.createFile(workbook, file);

        } catch (IOException ex) {

            throw new ExcelException("Erro ao escrever dados na planilha", ex);
        }

    }

    private void createFile(XSSFWorkbook workbook, File file) {

        try {

            FileOutputStream outFile = new FileOutputStream(new File(file.getAbsolutePath().replace(file.getName(), FileUtils.rename(file.getName()))));
            workbook.write(outFile);
            workbook.close();
            File processor = FileUtils.createDirectory("planilhas", "processados");
            FileUtils.moveFile(file.getAbsolutePath(), processor.getAbsolutePath().concat(FileSystems.getDefault().getSeparator()).concat(file.getName()));
        } catch (IOException ex) {
            throw new ExcelException("Erro ao criar planilha", ex);
        }
    }

    private Dados mountData(List<Cell> cells) {

        Dados dados = new Dados();

        if (Cell.CELL_TYPE_NUMERIC == cells.get(1).getCellType()) {
            dados.setNumeroImovel(Utils.convertToLong(cells.get(1).getNumericCellValue()));
        }

        if (Cell.CELL_TYPE_NUMERIC == cells.get(6).getCellType()) {
            dados.setCep(Utils.convertToLong(cells.get(6).getNumericCellValue()));
        }

        return dados;
    }
}
