package io.github.gitchenjh.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 高节
 * @since 2023-03-12
 */
public class StringUtils {

    public static String trim(String str) {
        Pattern p = Pattern.compile("\t|\r|\n");
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }
}
