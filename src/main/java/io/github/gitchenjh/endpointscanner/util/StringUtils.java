package io.github.gitchenjh.endpointscanner.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:chenjh1993@qq.com">chenjh</a>
 * @since 0.1.0
 */
public class StringUtils {

    public static String trim(String str) {
        Pattern p = Pattern.compile("\t|\r|\n");
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }
}
