package bbr.com.crawler.Utils;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

public class Utils {

    public static String rename(File file) {

        String extension = FilenameUtils.getExtension(file.getAbsolutePath());
        String name = file.getName();
        name = removeExtension(name, extension);
        name = addPrefix(name);

        return file.getAbsolutePath().replace(file.getName(), name.concat(".".concat(extension)));
    }

    public static List<?> toList(Iterator<?> iterator) {
        return IteratorUtils.toList(iterator);
    }

    public static Iterator<?> toIterator(List<?> list) {
        return list.iterator();
    }

    public static long convertToLong(double number) {
        return Double.valueOf(number).longValue();
    }

    private static String addPrefix(String name) {

        return name.concat("_").concat(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
    }

    private static String removeExtension(String name, String extension) {
        return name.replace(".".concat(extension), "");
    }
}