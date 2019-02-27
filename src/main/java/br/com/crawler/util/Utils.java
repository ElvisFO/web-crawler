package br.com.crawler.util;

import org.apache.commons.collections4.IteratorUtils;

import java.util.Iterator;
import java.util.List;

/**
 * @author Elvis Fernandes on 24/02/19
 */
public class Utils {

    public static List<?> toList(Iterator<?> iterator) {
        return IteratorUtils.toList(iterator);
    }

    public static Iterator<?> toIterator(List<?> list) {
        return list.iterator();
    }

    public static long convertToLong(double number) {
        return Double.valueOf(number).longValue();
    }

}