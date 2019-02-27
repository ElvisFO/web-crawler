package br.com.crawler.util;

import com.sun.javafx.PlatformUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

/**
 * @author Elvis Fernandes on 24/02/19
 */
public class FileUtils {

    private static String getUserHomeDirectory() {

        if(PlatformUtil.isWindows()) {
            return System.getProperty("user.home");
        }
        return System.getenv("HOME");
    }

    public static File createDirectory(String directory, String subDirectorys) throws IOException {

        Path processor = FileSystems.getDefault().getPath(getUserHomeDirectory(), directory, subDirectorys);

        if(!Files.exists(processor)) {
            Files.createDirectory(processor);
        }

        return processor.toFile();
    }

    public static void deleteDirectory(String directory) throws IOException {

        if(Files.exists(Paths.get(directory))) {
            Files.delete(Paths.get(directory));
        }
    }

    public static void moveFile(String source, String destiny) throws IOException {

        Files.move(Paths.get(source), Paths.get(destiny), StandardCopyOption.REPLACE_EXISTING);
    }

    public static File findFileByName(String name) throws IOException {

        Path directory = FileSystems.getDefault().getPath(getUserHomeDirectory(), "planilhas");

        if(!Files.exists(directory)) {
            throw new IOException("Diretório planilhas não existe.");
        }

        File[] files = directory.toFile().listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().equals(name);
            }
        });

        if(files == null || files.length == 0) {
            throw new IOException("Arquivo para consulta não encontrado.");
        }

        return files[0];
    }

    public static String rename(String name) {

        return UUID.randomUUID().toString().concat("_").concat(name);
    }
}